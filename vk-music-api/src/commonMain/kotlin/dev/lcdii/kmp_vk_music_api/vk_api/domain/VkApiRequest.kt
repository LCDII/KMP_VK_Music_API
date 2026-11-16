package dev.lcdii.kmp_vk_music_api.vk_api.domain

import dev.lcdii.kmp_vk_music_api.core.AUDIO_COUNT_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_ADD_TO_PLAYLIST_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_ADD_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_CREATE_PLAYLIST_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_DELETE_PLAYLIST_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_DELETE_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_GET_BY_ID_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_GET_POPULAR_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_GET_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_PLAYLIST_GET_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_REMOVE_FROM_PLAYLIST_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_SEARCH_ALBUMS_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_SEARCH_PLAYLIST_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_SEARCH_URL
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_AUDIO_AUTOCOMPLETE_MODE
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_AUDIO_COUNT
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_AUDIO_OFFSET
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_AUDIO_SORT_MODE
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_PLAYLIST_COUNT
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_PLAYLIST_OFFSET
import dev.lcdii.kmp_vk_music_api.core.PROFILE_INFO_URL

internal enum class HttpMethod {
    GET,
    POST
}

internal interface VkApiRequestParameters

public class PlaylistAttributes(
    public val playlistId: Int,
    public val accessKey: String
)

internal sealed interface VkApiRequest {
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

    data class Authenticate(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.GET
        override val path = "https://oauth.vk.com/token"

        data class Parameters(
            val login: String,
            val password: String,
            val clientId: String,
            val clientSecret: String,
            val successToken: String? = null,
            val code: String? = null
        ) : VkApiRequestParameters

        constructor(
            login: String,
            password: String,
            appClient: AppClient,
            successToken: String? = null,
            code: String? = null,
            headers: Map<String, String> = emptyMap()
        ) : this(
            parameters = Parameters(
                login = login,
                password = password,
                clientId = appClient.clientId,
                clientSecret = appClient.clientSecret,
                successToken = successToken,
                code = code
            ),
            headers = headers
        )
    }

    data class ValidatePhone(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.GET
        override val path = "auth.validatePhone"

        data class Parameters(
            val sid: String
        ) : VkApiRequestParameters

        constructor(
            sid: String,
            headers: Map<String, String> = emptyMap()
        ) : this(
            parameters = Parameters(sid),
            headers = headers
        )
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

        constructor(
            ownerId: Int,
            count: Int?,
            offset: Int?,
            playlistAttributes: PlaylistAttributes? = null,
            headers: Map<String, String> = emptyMap()
        ) : this(
            parameters = Parameters(
                ownerId = ownerId,
                count = count ?: DEFAULT_AUDIO_COUNT,
                offset = offset ?: DEFAULT_AUDIO_OFFSET,
                playlistAttributes = playlistAttributes
            ),
            headers = headers
        )
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

        constructor(
            ownerId: Int,
            count: Int?,
            offset: Int?,
            headers: Map<String, String> = emptyMap()
        ) : this(
            parameters = Parameters(
                ownerId = ownerId,
                count = count ?: DEFAULT_PLAYLIST_COUNT,
                offset = offset ?: DEFAULT_PLAYLIST_OFFSET
            ),
            headers = headers
        )
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

        constructor(
            query: String,
            count: Int? = DEFAULT_AUDIO_COUNT,
            offset: Int? = DEFAULT_AUDIO_OFFSET,
            sort: Int = DEFAULT_AUDIO_SORT_MODE,
            autocomplete: Int = DEFAULT_AUDIO_AUTOCOMPLETE_MODE,
            headers: Map<String, String> = emptyMap()
        ) : this(
            parameters = Parameters(
                query = query,
                count = count ?: DEFAULT_AUDIO_COUNT,
                offset = offset ?: DEFAULT_AUDIO_OFFSET,
                sort = sort,
                autocomplete = autocomplete
            ),
            headers = headers
        )
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

        constructor(
            query: String,
            count: Int?,
            offset: Int?,
            headers: Map<String, String> = emptyMap()
        ) : this(
            parameters = Parameters(
                query = query,
                count = count ?: DEFAULT_PLAYLIST_COUNT,
                offset = offset ?: DEFAULT_PLAYLIST_OFFSET
            ),
            headers = headers
        )
    }

    data class GetById(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ): VkApiRequest {
        override val method = HttpMethod.GET
        override val path = AUDIO_GET_BY_ID_URL

        data class Parameters(
            val audioIds: List<String>
        ) : VkApiRequestParameters

        constructor(
            audioIds: List<String>,
            headers: Map<String, String> = emptyMap()
        ) : this(Parameters(audioIds), headers)
    }

    data class SearchAlbums(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.GET
        override val path = AUDIO_SEARCH_ALBUMS_URL

        data class Parameters(
            val query: String,
            val count: Int = DEFAULT_PLAYLIST_COUNT,
            val offset: Int = DEFAULT_PLAYLIST_OFFSET
        ) : VkApiRequestParameters

        constructor(
            query: String,
            count: Int?,
            offset: Int?,
            headers: Map<String, String> = emptyMap()
        ) : this(
            parameters = Parameters(
                query = query,
                count = count ?: DEFAULT_PLAYLIST_COUNT,
                offset = offset ?: DEFAULT_PLAYLIST_OFFSET
            ),
            headers = headers
        )
    }

    data class GetPopular(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.GET
        override val path = AUDIO_GET_POPULAR_URL

        data class Parameters(
            val count: Int = DEFAULT_AUDIO_COUNT,
            val offset: Int = DEFAULT_AUDIO_OFFSET
        ) : VkApiRequestParameters

        constructor(
            count: Int?,
            offset: Int?,
            headers: Map<String, String> = emptyMap()
        ) : this(
            parameters = Parameters(
                count ?: DEFAULT_AUDIO_COUNT,
                offset ?: DEFAULT_AUDIO_OFFSET
            ),
            headers = headers
        )
    }

    data class AddAudio(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.POST
        override val path = AUDIO_ADD_URL

        data class Parameters(
            val ownerId: Int,
            val audioId: Int
        ) : VkApiRequestParameters

        constructor(
            ownerId: Int,
            audioId: Int,
            headers: Map<String, String> = emptyMap()
        ) : this(Parameters(ownerId, audioId), headers)
    }

    data class AddAudiosToPlaylist(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.POST
        override val path = AUDIO_ADD_TO_PLAYLIST_URL

        data class Parameters(
            val ownerId: Int,
            val playlistId: Int,
            val audioIds: List<String>
        ) : VkApiRequestParameters

        constructor(
            ownerId: Int,
            playlistId: Int,
            audioIds: List<String>,
            headers: Map<String, String> = emptyMap()
        ) : this(Parameters(ownerId, playlistId, audioIds), headers)
    }

    data class DeleteAudio(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.POST
        override val path = AUDIO_DELETE_URL

        data class Parameters(
            val ownerId: Int,
            val audioId: Int
        ) : VkApiRequestParameters

        constructor(
            ownerId: Int,
            audioId: Int,
            headers: Map<String, String> = emptyMap()
        ) : this(Parameters(ownerId, audioId), headers)
    }

    data class RemoveAudiosFromPlaylist(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.POST
        override val path = AUDIO_REMOVE_FROM_PLAYLIST_URL

        data class Parameters(
            val ownerId: Int,
            val playlistId: Int,
            val audioIds: List<String>
        ) : VkApiRequestParameters

        constructor(
            ownerId: Int,
            playlistId: Int,
            audioIds: List<String>,
            headers: Map<String, String> = emptyMap()
        ) : this(Parameters(ownerId, playlistId, audioIds), headers)
    }

    data class CreatePlaylist(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.POST
        override val path = AUDIO_CREATE_PLAYLIST_URL

        data class Parameters(
            val ownerId: Int,
            val title: String,
            val description: String? = null
        ) : VkApiRequestParameters

        constructor(
            ownerId: Int,
            title: String,
            description: String? = null,
            headers: Map<String, String> = emptyMap()
        ) : this(Parameters(ownerId, title, description), headers)
    }

    data class DeletePlaylist(
        override val parameters: Parameters,
        override val headers: Map<String, String> = emptyMap()
    ) : VkApiRequest {
        override val method = HttpMethod.POST
        override val path = AUDIO_DELETE_PLAYLIST_URL

        data class Parameters(
            val ownerId: Int,
            val playlistId: Int
        ) : VkApiRequestParameters

        constructor(
            ownerId: Int,
            playlistId: Int,
            headers: Map<String, String> = emptyMap()
        ) : this(Parameters(ownerId, playlistId), headers)
    }
}
