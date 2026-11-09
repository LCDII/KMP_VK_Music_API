package dev.lcdii.kmp_vk_music_api.vk_api.domain

import dev.lcdii.kmp_vk_music_api.core.DEFAULT_V
import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.EmptyResult
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.core.USER_AGENT_KEY
import dev.lcdii.kmp_vk_music_api.core.VkApiError
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.AuthPayload
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.VkApiResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import kotlinx.serialization.json.Json

internal class AuthFlow(
    private val httpClient: HttpClient,
    private val appClient: AppClient,
    private val json: Json,
    private val login: String,
    private val password: String,
    private val captchaResolver: VkCaptchaResolver,
    private val validationResolver: VkValidationResolver,
) {
    private var successToken: String? = null
    private var validationCode: String? = null
    private var pendingValidation: VkApiError.Auth.Validation? = null

    suspend fun run(): Result<AuthPayload, DataError> {
        while (true) {
            val request = VkApiRequest.Authenticate(
                login = login,
                password = password,
                appClient = appClient,
                successToken = successToken,
                code = validationCode,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent),
            )

            when (val result = safeCall<AuthPayload>(json) {
                makeAuthenticateRequest(request)
            }) {
                is Result.Success -> return result

                is Result.Error -> {
                    val authError = (
                        (result.error as? DataError.Remote.Api)?.error as? VkApiError.Auth
                    ) ?: return Result.Error(result.error)

                    when (val action = handle(authError, result.error)) {
                        is Result.Success -> Unit
                        is Result.Error -> return Result.Error(action.error)
                    }
                }
            }
        }
    }

    private suspend fun handle(
        error: VkApiError.Auth,
        originalError: DataError,
    ): EmptyResult<DataError> {
        return when (error) {
            is VkApiError.Auth.Captcha -> {
                when (val result = captchaResolver.resolve(error.redirectUri)) {
                    is Result.Success -> {
                        successToken = result.data
                        Result.Success(Unit)
                    }

                    is Result.Error -> Result.Error(result.error)
                }
            }

            is VkApiError.Auth.Validation -> {
                pendingValidation = error

                when (val phoneResult = makeValidationRequest(error.sid)) {
                    is Result.Error -> Result.Error(phoneResult.error)
                    is Result.Success -> requestCode(error)
                }
            }

            is VkApiError.Auth.InvalidValidationCode -> {
                val validation = pendingValidation
                    ?: return Result.Error(originalError)

                requestCode(
                    validation = validation,
                    description = error.description ?: "Invalid validation code"
                )
            }

            is VkApiError.Auth.InvalidClient,
            is VkApiError.Auth.InvalidToken,
            is VkApiError.Auth.Unknown -> Result.Error(originalError)
        }
    }

    private suspend fun requestCode(
        validation: VkApiError.Auth.Validation,
        description: String? = validation.description,
    ): EmptyResult<DataError> {
        val prompt = validation.copy(description = description)

        return when (val result = validationResolver.resolve(prompt)) {
            is Result.Success -> {
                validationCode = result.data
                Result.Success(Unit)
            }

            is Result.Error -> Result.Error(result.error)
        }
    }

    private suspend fun makeAuthenticateRequest(
        request: VkApiRequest.Authenticate
    ) = httpClient.get(request.path) {
        for ((key, value) in request.headers) {
            header(key, value)
        }
        parameter("grant_type", "password")
        parameter("client_id", request.parameters.clientId)
        parameter("client_secret", request.parameters.clientSecret)
        parameter("username", request.parameters.login)
        parameter("password", request.parameters.password)
        parameter("scope", "audio,offline")
        parameter("2fa_supported", 1)
        parameter("force_sms", 1)
        parameter("v", DEFAULT_V)
        request.parameters.successToken?.let { parameter("success_token", it) }
        request.parameters.code?.let { parameter("code", it) }
    }

    private suspend fun makeValidationRequest(
        sid: String
    ): Result<VkApiResponse.ValidatePhoneResponse, DataError.Remote> {
        val request = VkApiRequest.ValidatePhone(
            sid = sid,
            headers = mapOf(USER_AGENT_KEY to appClient.userAgent),
        )

        return safeCall<VkApiResponse.ValidatePhoneResponse>(json) {
            httpClient.get(request.path) {
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("sid", request.parameters.sid)
                parameter("v", DEFAULT_V)
            }
        }
    }
}
