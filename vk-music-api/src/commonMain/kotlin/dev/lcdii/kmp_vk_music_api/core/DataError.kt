package dev.lcdii.kmp_vk_music_api.core

public sealed interface DataError : Error {
    public sealed interface Remote : DataError {
        public data object RequestTimeout : Remote
        public data object TooManyRequests : Remote
        public data object NoInternet : Remote
        public data object Server : Remote
        public data object Serialization : Remote
        public data object Unknown : Remote
        public data object NotAuthorized : Remote

        public data class Api(
            val error: VkApiError
        ) : Remote

        public data class CaptchaFailed(
            val msg: String
        ) : Remote

    }

    public sealed interface Local: DataError {
        public data class BrowserNotFound(
            val msg: String
        ) : Local

    }
}
