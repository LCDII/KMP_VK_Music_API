package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.vk_api.components.AUDIO_COUNT_URL
import dev.lcdii.kmp_vk_music_api.vk_api.components.AUDIO_GET_URL
import dev.lcdii.kmp_vk_music_api.vk_api.components.AUDIO_PLAYLIST_GET_URL
import dev.lcdii.kmp_vk_music_api.vk_api.components.AUDIO_SEARCH_PLAYLIST_URL
import dev.lcdii.kmp_vk_music_api.vk_api.components.AUDIO_SEARCH_URL
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_AUDIO_AUTOCOMPLETE_MODE
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_AUDIO_COUNT
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_AUDIO_OFFSET
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_AUDIO_SORT_MODE
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_PLAYLIST_COUNT
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_PLAYLIST_OFFSET
import dev.lcdii.kmp_vk_music_api.vk_api.components.PROFILE_INFO_URL

enum class HttpMethod {
    GET,
    POST
}

interface VkApiRequestParameters

data class PlaylistAttributes(
    val playlistId: Int,
    val accessKey: String
)

sealed interface VkApiRequest {
    val method: HttpMethod
    val path: String
    val headers: Map<String, String>
    val parameters: VkApiRequestParameters

    data class GetProfileInfo(
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.GET
        override val path = PROFILE_INFO_URL
        override val parameters = Parameters

        data object Parameters : VkApiRequestParameters
    }

    data class GetUsersAudioCount(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.GET
        override val path = AUDIO_COUNT_URL

        data class Parameters(
            val ownerId: Int
        ) : VkApiRequestParameters

        constructor(
            ownerId: Int,
            headers: Map<String, String> = emptyMap()
        ) : this(
            parameters = Parameters(ownerId),
            headers = headers
        )
    }

    data class GetUsersAudio(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.GET
        override val path = AUDIO_GET_URL

        data class Parameters(
            val ownerId: Int,
            val count: Int = DEFAULT_AUDIO_COUNT,
            val offset: Int = DEFAULT_AUDIO_OFFSET,
            val playlistAttributes: PlaylistAttributes? = null
        ) : VkApiRequestParameters
    }

    data class GetUsersPlaylists(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.GET
        override val path = AUDIO_PLAYLIST_GET_URL

        data class Parameters(
            val ownerId: Int,
            val count: Int = DEFAULT_PLAYLIST_COUNT,
            val offset: Int = DEFAULT_PLAYLIST_OFFSET
        ) : VkApiRequestParameters
    }

    data class SearchAudio(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.GET
        override val path = AUDIO_SEARCH_URL

        data class Parameters(
            val query: String,
            val count: Int = DEFAULT_AUDIO_COUNT,
            val offset: Int = DEFAULT_AUDIO_OFFSET,
            val sort: Int = DEFAULT_AUDIO_SORT_MODE,
            val autocomplete: Int = DEFAULT_AUDIO_AUTOCOMPLETE_MODE
        ) : VkApiRequestParameters
    }

    data class SearchPlaylists(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.GET
        override val path = AUDIO_SEARCH_PLAYLIST_URL

        data class Parameters(
            val query: String,
            val count: Int = DEFAULT_PLAYLIST_COUNT,
            val offset: Int = DEFAULT_PLAYLIST_OFFSET
        ) : VkApiRequestParameters
    }
}
