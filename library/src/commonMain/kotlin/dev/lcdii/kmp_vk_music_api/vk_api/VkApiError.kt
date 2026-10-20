package dev.lcdii.kmp_vk_music_api.vk_api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class TextExceptionType{
    NEED_CAPTCHA,
    NEED_VALIDATION,
    INVALID_TOKEN,
    UNKNOWN;
    companion object{
        fun fromString(string: String): TextExceptionType = when(string) {
            "need_captcha" -> TextExceptionType.NEED_CAPTCHA
            "need_validation" -> TextExceptionType.NEED_VALIDATION
            "invalid_token" -> TextExceptionType.INVALID_TOKEN
            else -> TextExceptionType.UNKNOWN
        }
    }
}

sealed interface VkApiError{
    data class CodeException(
        val code: Int,
        val message: String,
        //TODO request params
    ): VkApiError

    data class TextException(
        val type: TextExceptionType,
        val description: String
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
