package dev.lcdii.kmp_vk_music_api.vk_api.data

import dev.lcdii.kmp_vk_music_api.vk_api.TextErrorType
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiError
import dev.lcdii.kmp_vk_music_api.vk_api.core.DataError
import dev.lcdii.kmp_vk_music_api.vk_api.core.Result
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

suspend inline fun <reified T> safeCall(
    json: Json,
    crossinline execute: suspend () -> HttpResponse
): Result<T, DataError.Remote> {
    val response = try {
        execute()
    } catch (_: SocketTimeoutException) {
        return Result.Error(DataError.Remote.RequestTimeout)
    } catch (_: UnresolvedAddressException) {
        return Result.Error(DataError.Remote.NoInternet)
    } catch (_: Exception) {
        currentCoroutineContext().ensureActive()
        return Result.Error(DataError.Remote.Unknown)
    }

    return responseToResult(response, json)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse,
    json: Json
): Result<T, DataError.Remote> {
    val body = try {
        response.bodyAsText()
    } catch (_: Exception) {
        currentCoroutineContext().ensureActive()
        return Result.Error(DataError.Remote.Serialization)
    }

    val root = try {
        json.parseToJsonElement(body) as? JsonObject
            ?: return Result.Error(DataError.Remote.Serialization)
    } catch (_: SerializationException) {
        return Result.Error(DataError.Remote.Serialization)
    }

    parseVkApiError(root)?.let { error ->
        return Result.Error(DataError.Remote.Api(error))
    }

    return when (response.status.value) {
        in 200..299 -> {
            val responseJson = root["response"] ?: root

            try {
                Result.Success(json.decodeFromJsonElement<T>(responseJson))
            } catch (_: SerializationException) {
                Result.Error(DataError.Remote.Serialization)
            }
        }
        408 -> Result.Error(DataError.Remote.RequestTimeout)
        429 -> Result.Error(DataError.Remote.TooManyRequests)
        in 500..599 -> Result.Error(DataError.Remote.Server)
        else -> Result.Error(DataError.Remote.Unknown)
    }
}

@PublishedApi
internal fun parseVkApiError(root: JsonObject): VkApiError? {
    val error = root["error"] ?: return null

    return when (error) {
        is JsonObject -> when {
            "error_code" in error -> VkApiError.CodeError(
                code = error.getValue("error_code").jsonPrimitive.int,
                message = error.getValue("error_msg").jsonPrimitive.content
            )

            "error" in error -> VkApiError.TextError(
                type = TextErrorType.fromString(
                    error.getValue("error").jsonPrimitive.content
                ),
                description = error["error_description"]?.jsonPrimitive?.content,
                redirectUri = error["redirect_uri"]?.jsonPrimitive?.content,
                validationType = error["validation_type"]?.jsonPrimitive?.content,
                validationSid = error["validation_sid"]?.jsonPrimitive?.content
            )

            else -> VkApiError.UnknownError(error.toString())
        }

        is JsonPrimitive -> VkApiError.TextError(
            type = TextErrorType.fromString(error.content),
            description = root["error_description"]?.jsonPrimitive?.content,
            redirectUri = root["redirect_uri"]?.jsonPrimitive?.content,
            validationType = root["validation_type"]?.jsonPrimitive?.content,
            validationSid = root["validation_sid"]?.jsonPrimitive?.content
        )

        else -> VkApiError.UnknownError(root.toString())
    }
}
