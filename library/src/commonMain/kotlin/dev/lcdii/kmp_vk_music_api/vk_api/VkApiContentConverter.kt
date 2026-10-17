package dev.lcdii.kmp_vk_music_api.vk_api

import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.serialization.ContentConverter
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.core.readText
import io.ktor.utils.io.readBuffer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class VkApiContentConverter(
    private val json: Json
) : ContentConverter {
    private val delegate = KotlinxSerializationConverter(json)

    override suspend fun deserialize(
        charset: Charset,
        typeInfo: TypeInfo,
        content: ByteReadChannel
    ): Any? {
        val body = content.readBuffer().readText(charset)
        val response = json.parseToJsonElement(body).jsonObject["response"]

        return delegate.deserialize(
            charset = charset,
            typeInfo = typeInfo,
            content = ByteReadChannel(response.toString().encodeToByteArray())
        )
    }

    override suspend fun serialize(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any?
    ): OutgoingContent {
        return delegate.serialize(contentType, charset, typeInfo, value)
    }
}
