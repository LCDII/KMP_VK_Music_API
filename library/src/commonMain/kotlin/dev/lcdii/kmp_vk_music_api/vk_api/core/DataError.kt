package dev.lcdii.kmp_vk_music_api.vk_api.core

import dev.lcdii.kmp_vk_music_api.vk_api.VkApiError

sealed interface DataError : Error {
    sealed interface Remote : DataError {
        data object RequestTimeout : Remote
        data object TooManyRequests : Remote
        data object NoInternet : Remote
        data object Server : Remote
        data object Serialization : Remote
        data object Unknown : Remote
        data object NotAuthorized : Remote

        data class Api(
            val error: VkApiError
        ) : Remote
    }
}
