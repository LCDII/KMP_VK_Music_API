package dev.lcdii.kmp_vk_music_api.vk_api

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

private const val PROFILE_INFO_URL = "account.getProfileInfo"
private const val AUDIO_COUNT_URL = "audio.getCount"
private const val AUDIO_GET_URL = "audio.get"
private const val AUDIO_SEARCH_URL = "audio.search"
private const val AUDIO_PLAYLIST_GET_URL = "audio.getPlaylists"
private const val AUDIO_SEARCH_PLAYLIST_URL = "audio.searchPlaylists"
private const val DEFAULT_AUDIO_COUNT = 50
private const val DEFAULT_AUDIO_OFFSET = 0
private const val DEFAULT_AUDIO_SORT_MODE = 0
private const val DEFAULT_AUDIO_AUTOCOMPLETE_MODE = 1

private const val DEFAULT_PLAYLIST_COUNT = 50
private const val DEFAULT_PLAYLIST_OFFSET = 0

class VkApiRequestBuilder {
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
        playlistId: Int? = null,//TODO both in one
        accessKey: Int? = null
    ) : VkApiRequest {
        val otherParams = mapOf<String, JsonElement>(
            ("owner_id" to JsonPrimitive(userId)),
            ("count" to JsonPrimitive(count)),
            ("offset" to JsonPrimitive(offset))
        )
        var params = Params(other = otherParams)
        playlistId?.let { it ->
            params = params.addParam("album_id", JsonPrimitive(it))
        }
        accessKey?.let { it->
            params = params.addParam("access_key", JsonPrimitive(it))
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
