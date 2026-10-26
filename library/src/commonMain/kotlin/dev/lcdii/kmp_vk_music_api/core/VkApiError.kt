package dev.lcdii.kmp_vk_music_api.core

public enum class TextErrorType{
    NEED_CAPTCHA,
    NEED_VALIDATION,
    INVALID_REQUEST,
    INVALID_CLIENT,
    INVALID_TOKEN,
    UNKNOWN;
    public companion object{
        public fun fromString(string: String): TextErrorType = when(string) {
            "need_captcha" -> TextErrorType.NEED_CAPTCHA
            "need_validation" -> TextErrorType.NEED_VALIDATION
            "invalid_request" -> TextErrorType.INVALID_REQUEST
            "invalid_client" -> TextErrorType.INVALID_CLIENT
            "invalid_token" -> TextErrorType.INVALID_TOKEN
            else -> TextErrorType.UNKNOWN
        }
    }
}

public sealed interface VkApiError : Error {
    public data class CodeError(
        val code: Int,
        val message: String,
        //TODO request params
    ): VkApiError

    public data class TextError(
        val type: TextErrorType,
        val description: String? = null,
        val redirectUri: String? = null,
        val validationType: String? = null,
        val validationSid: String? = null
    ): VkApiError

    public data class UnknownError(
        val json: String
    ): VkApiError
}

