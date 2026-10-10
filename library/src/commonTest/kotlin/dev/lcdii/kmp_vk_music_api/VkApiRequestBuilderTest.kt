package dev.lcdii.kmp_vk_music_api

import dev.lcdii.kmp_vk_music_api.vk_api.VkApiRequestBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class VkApiRequestBuilderTest {
    @Test
    fun getProfileInfoRequestBuiltValid() {
        val request = VkApiRequestBuilder().buildGetProfileInfoRequest()
        val result = "VkApiRequest(method=GET, url=https://api.vk.com/method/account.getProfileInfo," +
                " headers={}, params=Params(https=1, lang=ru, extended=1, v=5.131, other={}))"
        assertEquals(request.toString(), result)
    }

    @Test
    fun getUsersAudiosCountRequestBuiltValid() {
        val userId = 123
        val request = VkApiRequestBuilder().buildGetUsersAudiosCountRequest(userId)
        val result = "VkApiRequest(method=GET, url=https://api.vk.com/method/audio.getCount," +
                " headers={}, params=Params(https=1, lang=ru, extended=1, v=5.131, other={owner_id=$userId}))"
        assertEquals(request.toString(), result)
    }

    @Test
    fun getUsersAudioRequestWithoutPlaylistIdBuiltValid() {
        val userId = 123
        val request = VkApiRequestBuilder().buildGetUsersAudioRequest(userId)
        val result = "VkApiRequest(method=GET, url=https://api.vk.com/method/audio.get," +
                " headers={}, params=Params(https=1, lang=ru, extended=1, v=5.131, " +
                "other={owner_id=$userId, count=50, offset=0}))"
        assertEquals(request.toString(), result)
    }
    //TODO with playlistId

    @Test
    fun getUsersPlaylistsRequestBuiltValid() {
        val userId = 123
        val request = VkApiRequestBuilder().buildGetUsersPlaylistsRequest(userId)
        val result = "VkApiRequest(method=GET, url=https://api.vk.com/method/audio.getPlaylists," +
                " headers={}, params=Params(https=1, lang=ru, extended=1, v=5.131, " +
                "other={owner_id=$userId, count=50, offset=0}))"
        assertEquals(request.toString(), result)
    }

    @Test
    fun searchAudioRequestBuiltValid() {
        val query = "Take Me To The Sun"
        val request = VkApiRequestBuilder().buildSearchAudioRequest(query)
        val result = "VkApiRequest(method=GET, url=https://api.vk.com/method/audio.search," +
                " headers={}, params=Params(https=1, lang=ru, extended=1, v=5.131, " +
                "other={q=\"$query\", count=50, offset=0, sort=0, autocomplete=1}))"
        assertEquals(request.toString(), result)
    }

    @Test
    fun searchPlaylistsRequestBuiltValid() {
        val query = "Take Me To The Sun"
        val request = VkApiRequestBuilder().buildSearchPlaylistsRequest(query)
        val result = "VkApiRequest(method=GET, url=https://api.vk.com/method/audio.searchPlaylists," +
                " headers={}, params=Params(https=1, lang=ru, extended=1, v=5.131, " +
                "other={q=\"$query\", count=50, offset=0}))"
        assertEquals(request.toString(), result)
    }
}