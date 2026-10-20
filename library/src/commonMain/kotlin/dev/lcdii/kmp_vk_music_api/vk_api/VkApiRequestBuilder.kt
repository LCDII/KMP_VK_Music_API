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
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

data class PlaylistAttributes(
    val playlistId: Int,
    val accessKey: String
)

object VkApiRequestBuilder {
    fun buildGetProfileInfoRequest() =
        VkApiRequest().modifyUrl(urlContinuation = PROFILE_INFO_URL)

    fun buildGetUsersAudiosCountRequest(userId: Int) : VkApiRequest {
        val params = Params().addParam(
            key = "owner_id",
            element = JsonPrimitive(userId)
        )
        return VkApiRequest(params = params).modifyUrl(AUDIO_COUNT_URL)
    }

    fun buildGetUsersAudioRequest(
        userId: Int,
        count: Int = DEFAULT_AUDIO_COUNT,
        offset: Int = DEFAULT_AUDIO_OFFSET,
        playlistAttributes: PlaylistAttributes? = null
    ) : VkApiRequest {
        val otherParams = mapOf<String, JsonElement>(
            ("owner_id" to JsonPrimitive(userId)),
            ("count" to JsonPrimitive(count)),
            ("offset" to JsonPrimitive(offset))
        )
        var params = Params(other = otherParams)
        playlistAttributes?.let {
            params = params
                .addParam("album_id", JsonPrimitive(it.playlistId))
                .addParam("access_key", JsonPrimitive(it.accessKey))
        }
        return VkApiRequest(params = params).modifyUrl(AUDIO_GET_URL)
    }

    fun buildGetUsersPlaylistsRequest(
        userId: Int,
        count: Int = DEFAULT_PLAYLIST_COUNT,
        offset: Int = DEFAULT_PLAYLIST_OFFSET
    ) : VkApiRequest {
        val otherParams = mapOf<String, JsonElement>(
            ("owner_id" to JsonPrimitive(userId)),
            ("count" to JsonPrimitive(count)),
            ("offset" to JsonPrimitive(offset))
        )
        val params = Params(other = otherParams)
        return VkApiRequest(params = params).modifyUrl(AUDIO_PLAYLIST_GET_URL)
    }

    fun buildSearchAudioRequest(
        query: String,
        count: Int = DEFAULT_AUDIO_COUNT,
        offset: Int = DEFAULT_AUDIO_OFFSET
    ): VkApiRequest {
        val otherParams = mapOf<String, JsonElement>(
            ("q" to JsonPrimitive(query)),
            ("count" to JsonPrimitive(count)),
            ("offset" to JsonPrimitive(offset)),
            ("sort" to JsonPrimitive(DEFAULT_AUDIO_SORT_MODE)),
            ("autocomplete" to JsonPrimitive(DEFAULT_AUDIO_AUTOCOMPLETE_MODE))
        )
        val params = Params(other = otherParams)
        return VkApiRequest(params = params).modifyUrl(AUDIO_SEARCH_URL)
    }

    fun buildSearchPlaylistsRequest(
        query: String,
        count: Int = DEFAULT_PLAYLIST_COUNT,
        offset: Int = DEFAULT_PLAYLIST_OFFSET
    ) : VkApiRequest {
        val otherParams = mapOf<String, JsonElement>(
            ("q" to JsonPrimitive(query)),
            ("count" to JsonPrimitive(count)),
            ("offset" to JsonPrimitive(offset))
        )
        val params = Params(other = otherParams)
        return VkApiRequest(params = params).modifyUrl(AUDIO_SEARCH_PLAYLIST_URL)
    }
}
