package dev.lcdii.kmp_vk_music_api.vk_api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import dev.lcdii.kmp_vk_music_api.vk_api.core.Error

enum class TextErrorType{
    NEED_CAPTCHA,
    NEED_VALIDATION,
    INVALID_REQUEST,
    INVALID_CLIENT,
    INVALID_TOKEN,
    UNKNOWN;
    companion object{
        fun fromString(string: String): TextErrorType = when(string) {
            "need_captcha" -> TextErrorType.NEED_CAPTCHA
            "need_validation" -> TextErrorType.NEED_VALIDATION
            "invalid_request" -> TextErrorType.INVALID_REQUEST
            "invalid_client" -> TextErrorType.INVALID_CLIENT
            "invalid_token" -> TextErrorType.INVALID_TOKEN
            else -> TextErrorType.UNKNOWN
        }
    }
}

sealed interface VkApiError : Error {
    data class CodeError(
        val code: Int,
        val message: String,
        //TODO request params
    ): VkApiError

    data class TextError(
        val type: TextErrorType,
        val description: String? = null,
        val redirectUri: String? = null,
        val validationType: String? = null,
        val validationSid: String? = null
    ): VkApiError

    data class UnknownError(
        val json: String
    ): VkApiError
}

object VkApiErrorSerializer : KSerializer<VkApiError> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("VkApiError")

    override fun serialize(
        encoder: Encoder,
        value: VkApiError
    ) {
        TODO("Not yet implemented")
    }

    override fun deserialize(decoder: Decoder): VkApiError {
        TODO("Not yet implemented")
    }

}
