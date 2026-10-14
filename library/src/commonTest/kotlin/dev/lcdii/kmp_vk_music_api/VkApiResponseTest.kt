package dev.lcdii.kmp_vk_music_api

import com.goncalossilva.resources.Resource
import dev.lcdii.kmp_vk_music_api.components.responses.audio.*
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiResponse
import dev.lcdii.kmp_vk_music_api.vk_api.decodeResponse
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

private val testJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

class VkApiResponseTest {
    @Test
    fun audioGetCountResponseDecodedValid() {
        val actual = testJson.decodeResponse<VkApiResponse.AudioGetCount>(
            Resource("json_responses/audio/get_count.json").readText()
        )
        assertEquals(expected = EXPECTED_GET_COUNT, actual = actual)
    }
    @Test
    fun audioGetResponseDecodeValid() {
        val actual = testJson.decodeResponse<VkApiResponse.AudioGet>(
            Resource("json_responses/audio/get.json").readText()
        )
        assertEquals(expected = EXPECTED_GET, actual = actual)
    }
    //TODO more combinations
    @Test
    fun audioSearchResponseDecodeValid() {
        val actual = testJson.decodeResponse<VkApiResponse.AudioSearch>(
            Resource("json_responses/audio/search.json").readText()
        )
        assertEquals(expected = EXPECTED_SEARCH, actual = actual)
    }
    @Test
    fun audioGetByIdResponseDecodeValid() {
        val actual = testJson.decodeResponse<VkApiResponse.AudioGetById>(
            Resource("json_responses/audio/get_by_id.json").readText()
        )
        assertEquals(expected = EXPECTED_GET_BY_ID, actual = actual)
    }
    @Test
    fun audioGetPlaylistsResponseDecodeValid() {
        val actual = testJson.decodeResponse<VkApiResponse.AudioGetPlayLists> (
            Resource("json_responses/audio/get_playlists.json").readText()
        )
        assertEquals(expected = EXPECTED_GET_PLAYLISTS, actual = actual)
    }
    @Test
    fun audioSearchPlaylistsResponseDecodeValid() {
        val actual = testJson.decodeResponse<VkApiResponse.AudioSearchPlaylists>(
            Resource("json_responses/audio/search_playlists.json").readText()
        )
        assertEquals(expected = EXPECTED_SEARCH_PLAYLISTS, actual = actual)
    }
    @Test
    fun audioSearchAlbumsResponseDecodeValid() {
        val actual = testJson.decodeResponse<VkApiResponse.AudioSearchAlbums>(
            Resource("json_responses/audio/search_albums.json").readText()
        )
        assertEquals(expected = EXPECTED_SEARCH_ALBUMS, actual = actual)
    }
}
