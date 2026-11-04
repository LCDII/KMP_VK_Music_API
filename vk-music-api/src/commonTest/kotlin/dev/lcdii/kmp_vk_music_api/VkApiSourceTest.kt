package dev.lcdii.kmp_vk_music_api

import com.goncalossilva.resources.Resource
import dev.lcdii.kmp_vk_music_api.core.ACCESS_TOKEN_KEY
import dev.lcdii.kmp_vk_music_api.core.AUDIO_COUNT_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_GET_BY_ID_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_GET_POPULAR_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_GET_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_PLAYLIST_GET_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_SEARCH_ALBUMS_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_SEARCH_PLAYLIST_URL
import dev.lcdii.kmp_vk_music_api.core.AUDIO_SEARCH_URL
import dev.lcdii.kmp_vk_music_api.core.BASE_URL
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_AUDIO_AUTOCOMPLETE_MODE
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_AUDIO_COUNT
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_AUDIO_OFFSET
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_AUDIO_SORT_MODE
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_EXTENDED
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_HTTPS
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_LANG
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_PLAYLIST_COUNT
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_PLAYLIST_OFFSET
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_V
import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.PROFILE_INFO_URL
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.core.USER_AGENT_KEY
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.data.HttpClientFactory
import dev.lcdii.kmp_vk_music_api.vk_api.data.VkMobileAppClient
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.VkApiResponse
import dev.lcdii.kmp_vk_music_api.vk_api.domain.PlaylistAttributes
import dev.lcdii.kmp_vk_music_api.vk_api.domain.TokenStorage
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import kotlin.test.assertIs

private const val TEST_TOKEN: String = "token"
private const val USER_ID: Int = 1234
private const val AUDIO_OFFSET: Int = 50
private const val AUDIO_COUNT: Int = 50
private const val PLAYLIST_ID: Int = 10
private const val ACCESS_KEY: String = "1q2w3e"
private const val PLAYLIST_OFFSET: Int = 50
private const val PLAYLIST_COUNT: Int = 50
private const val QUERY: String = "ЭКСПОНАТ"
private const val SEARCH_AUDIO_OFFSET: Int = 50
private const val SEARCH_AUDIO_COUNT: Int = 50
private const val SEARCH_PLAYLIST_OFFSET: Int = 50
private const val SEARCH_PLAYLIST_COUNT: Int = 50
private const val SEARCH_ALBUMS_COUNT: Int = 50
private const val SEARCH_ALBUMS_OFFSET: Int = 50
private const val POPULAR_COUNT: Int = 50
private const val POPULAR_OFFSET: Int = 50
private val AUDIO_IDS: List<String> = listOf("123_456", "234_567", "345_678")


class VkApiSourceTest {
    //GET ==========================================================================================
    private class TestTokenStorage(
        private var token: String? = TEST_TOKEN
    ): TokenStorage {
        override suspend fun saveToken(token: String) {
            this.token = token
        }

        override suspend fun getToken(): String? {
            return this.token
        }

        override suspend fun clearToken() {
            token = null
        }

        override suspend fun replaceToken(token: String) {
            saveToken(token)
        }

    }

    private class TestEmptyTokenStorage(
        private var token: String? = null
    ): TokenStorage {
        override suspend fun saveToken(token: String) {
            this.token = token
        }

        override suspend fun getToken(): String? {
            return this.token
        }

        override suspend fun clearToken() {
            token = null
        }

        override suspend fun replaceToken(token: String) {
            saveToken(token)
        }

    }

    private fun createApiWithoutToken(): VkApiSource {
        val engine = MockEngine {
            error("error")
        }
        return VkApiSource(
            httpClient = HttpClientFactory.create(engine),
            tokenStorage = TestEmptyTokenStorage()
        )
    }



    @Test
    fun `getProfileInfo sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${PROFILE_INFO_URL}",request.url.encodedPath)

            respond(
                content = Resource("json_responses/account/get_profile_info.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.ProfileInfo>>(api.getProfileInfo())
    }

    @Test
    fun `getUsersAudioCount sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_COUNT_URL}",request.url.encodedPath)
            assertEquals(USER_ID.toString(), request.url.parameters["owner_id"])

            respond(
                content = Resource("json_responses/audio/get_count.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGetCount>>(
            api.getUsersAudioCount(USER_ID)
        )
    }

    @Test
    fun `getUsersAudio sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_GET_URL}",request.url.encodedPath)
            assertEquals(USER_ID.toString(), request.url.parameters["owner_id"])
            assertEquals(DEFAULT_AUDIO_COUNT.toString(), request.url.parameters["count"])
            assertEquals(DEFAULT_AUDIO_OFFSET.toString(), request.url.parameters["offset"])

            respond(
                content = Resource("json_responses/audio/get.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGet>>(api.getUsersAudio(USER_ID))
    }

    @Test
    fun `getUsersAudio with custom count and offset sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_GET_URL}",request.url.encodedPath)
            assertEquals(USER_ID.toString(), request.url.parameters["owner_id"])
            assertEquals(AUDIO_COUNT.toString(), request.url.parameters["count"])
            assertEquals(AUDIO_OFFSET.toString(), request.url.parameters["offset"])

            respond(
                content = Resource("json_responses/audio/get.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGet>>(
            api.getUsersAudio(USER_ID, AUDIO_COUNT, AUDIO_OFFSET)
        )
    }

    @Test
    fun `getUsersAudio from playlist sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_GET_URL}",request.url.encodedPath)
            assertEquals(USER_ID.toString(), request.url.parameters["owner_id"])
            assertEquals(DEFAULT_AUDIO_COUNT.toString(), request.url.parameters["count"])
            assertEquals(DEFAULT_AUDIO_OFFSET.toString(), request.url.parameters["offset"])
            assertEquals(PLAYLIST_ID.toString(), request.url.parameters["album_id"])
            assertEquals(ACCESS_KEY, request.url.parameters["access_key"])

            respond(
                content = Resource("json_responses/audio/get.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGet>>(api.getUsersAudio(userId = USER_ID,
            playlistAttributes = PlaylistAttributes(PLAYLIST_ID, ACCESS_KEY)))
    }

    @Test
    fun `getUsersPlaylists sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_PLAYLIST_GET_URL}",request.url.encodedPath)
            assertEquals(USER_ID.toString(), request.url.parameters["owner_id"])
            assertEquals(DEFAULT_PLAYLIST_COUNT.toString(), request.url.parameters["count"])
            assertEquals(DEFAULT_PLAYLIST_OFFSET.toString(), request.url.parameters["offset"])

            respond(
                content = Resource("json_responses/audio/get_playlists.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGetPlayLists>>(
            api.getUsersPlaylists(USER_ID)
        )
    }

    @Test
    fun `getUsersPlaylists with custom count and offset sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_PLAYLIST_GET_URL}",request.url.encodedPath)
            assertEquals(USER_ID.toString(), request.url.parameters["owner_id"])
            assertEquals(PLAYLIST_COUNT.toString(), request.url.parameters["count"])
            assertEquals(PLAYLIST_OFFSET.toString(), request.url.parameters["offset"])

            respond(
                content = Resource("json_responses/audio/get_playlists.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGetPlayLists>>(
            api.getUsersPlaylists(USER_ID, PLAYLIST_COUNT, PLAYLIST_OFFSET)
        )
    }

    @Test
    fun `searchAudio sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_SEARCH_URL}",request.url.encodedPath)
            assertEquals(QUERY, request.url.parameters["q"])
            assertEquals(DEFAULT_AUDIO_COUNT.toString(), request.url.parameters["count"])
            assertEquals(DEFAULT_AUDIO_OFFSET.toString(), request.url.parameters["offset"])
            assertEquals(DEFAULT_AUDIO_AUTOCOMPLETE_MODE.toString(), request.url.parameters["autocomplete"])
            assertEquals(DEFAULT_AUDIO_SORT_MODE.toString(), request.url.parameters["sort"])

            respond(
                content = Resource("json_responses/audio/search.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioSearch>>(api.searchAudio(QUERY))
    }

    @Test
    fun `searchAudio with custom offset and count sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_SEARCH_URL}",request.url.encodedPath)
            assertEquals(QUERY, request.url.parameters["q"])
            assertEquals(SEARCH_AUDIO_COUNT.toString(), request.url.parameters["count"])
            assertEquals(SEARCH_AUDIO_OFFSET.toString(), request.url.parameters["offset"])
            assertEquals(DEFAULT_AUDIO_AUTOCOMPLETE_MODE.toString(), request.url.parameters["autocomplete"])
            assertEquals(DEFAULT_AUDIO_SORT_MODE.toString(), request.url.parameters["sort"])

            respond(
                content = Resource("json_responses/audio/search.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioSearch>>(
            api.searchAudio(QUERY, PLAYLIST_COUNT, PLAYLIST_OFFSET)
        )
    }

    @Test
    fun `searchPlaylists sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_SEARCH_PLAYLIST_URL}",request.url.encodedPath)
            assertEquals(QUERY, request.url.parameters["q"])
            assertEquals(DEFAULT_PLAYLIST_COUNT.toString(), request.url.parameters["count"])
            assertEquals(DEFAULT_PLAYLIST_OFFSET.toString(), request.url.parameters["offset"])

            respond(
                content = Resource("json_responses/audio/search_playlists.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioSearchPlaylists>>(api.searchPlaylists(QUERY))
    }

    @Test
    fun `searchPlaylists with custom offset and count sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_SEARCH_PLAYLIST_URL}",request.url.encodedPath)
            assertEquals(QUERY, request.url.parameters["q"])
            assertEquals(SEARCH_PLAYLIST_COUNT.toString(), request.url.parameters["count"])
            assertEquals(SEARCH_PLAYLIST_OFFSET.toString(), request.url.parameters["offset"])

            respond(
                content = Resource("json_responses/audio/search_playlists.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioSearchPlaylists>>(
            api.searchPlaylists(QUERY, SEARCH_PLAYLIST_COUNT, SEARCH_PLAYLIST_OFFSET)
        )
    }

    @Test
    fun `getAudioById sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_GET_BY_ID_URL}",request.url.encodedPath)
            assertEquals(AUDIO_IDS.joinToString(","), request.url.parameters["audios"])

            respond(
                content = Resource("json_responses/audio/get_by_id.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGetById>>(
            api.getAudioById(AUDIO_IDS)
        )
    }

    @Test
    fun `getAudioById with empty ids have DataError` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_GET_BY_ID_URL}",request.url.encodedPath)
            assertEquals("", request.url.parameters["audios"])

            respond(
                content = Resource("json_responses/audio/get_by_id.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Error<DataError.Remote.Api>>(api.getAudioById(emptyList()))
    }

    @Test
    fun `searchAlbums sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_SEARCH_ALBUMS_URL}",request.url.encodedPath)
            assertEquals(QUERY, request.url.parameters["q"])
            assertEquals(DEFAULT_PLAYLIST_COUNT.toString(), request.url.parameters["count"])
            assertEquals(DEFAULT_PLAYLIST_OFFSET.toString(), request.url.parameters["offset"])

            respond(
                content = Resource("json_responses/audio/search_albums.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioSearchAlbums>>(api.searchAlbums(QUERY))
    }

    @Test
    fun `searchAlbums with custom offset and count sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_SEARCH_ALBUMS_URL}",request.url.encodedPath)
            assertEquals(QUERY, request.url.parameters["q"])
            assertEquals(SEARCH_ALBUMS_COUNT.toString(), request.url.parameters["count"])
            assertEquals(SEARCH_ALBUMS_OFFSET.toString(), request.url.parameters["offset"])

            respond(
                content = Resource("json_responses/audio/search_albums.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioSearchAlbums>>(
            api.searchAlbums(QUERY, SEARCH_PLAYLIST_COUNT, SEARCH_PLAYLIST_OFFSET)
        )
    }

    @Test
    fun `getPopular sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_GET_POPULAR_URL}",request.url.encodedPath)
            assertEquals(DEFAULT_AUDIO_COUNT.toString(), request.url.parameters["count"])
            assertEquals(DEFAULT_AUDIO_OFFSET.toString(), request.url.parameters["offset"])

            respond(
                content = Resource("json_responses/audio/get_popular.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGetPopular>>(api.getPopular())
    }

    @Test
    fun `getPopular with custom offset and count sends right request` () = runTest {
        val engine = MockEngine { request ->
            //standard
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(Url(BASE_URL).host, request.url.host)
            assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
            assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
            assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
            assertEquals(DEFAULT_V,request.url.parameters["v"])
            assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
            assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])

            //not standard
            assertEquals("/method/${AUDIO_GET_POPULAR_URL}",request.url.encodedPath)
            assertEquals(POPULAR_COUNT.toString(), request.url.parameters["count"])
            assertEquals(POPULAR_OFFSET.toString(), request.url.parameters["offset"])

            respond(
                content = Resource("json_responses/audio/get_popular.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGetPopular>>(
            api.getPopular(SEARCH_PLAYLIST_COUNT, SEARCH_PLAYLIST_OFFSET)
        )
    }
    
    //EMPTY TOKEN ===============================================================================
    @Test
    fun `getProfileInfo without token returns NotAuthorized` () = runTest {
        assertIs<Result.Error<DataError.Remote.NotAuthorized>>(
            createApiWithoutToken().getProfileInfo()
        )
    }

    @Test
    fun `getUsersAudioCount without token returns NotAuthorized` () = runTest {
        assertIs<Result.Error<DataError.Remote.NotAuthorized>>(
            createApiWithoutToken().getUsersAudioCount(USER_ID)
        )
    }

    @Test
    fun `getUsersAudio without token returns NotAuthorized` () = runTest {
        assertIs<Result.Error<DataError.Remote.NotAuthorized>>(
            createApiWithoutToken().getUsersAudio(USER_ID)
        )
    }

    @Test
    fun `getUsersPlaylists without token returns NotAuthorized` () = runTest {
        assertIs<Result.Error<DataError.Remote.NotAuthorized>>(
            createApiWithoutToken().getUsersPlaylists(USER_ID)
        )
    }

    @Test
    fun `searchAudio without token returns NotAuthorized` () = runTest {
        assertIs<Result.Error<DataError.Remote.NotAuthorized>>(
            createApiWithoutToken().searchAudio(QUERY)
        )
    }

    @Test
    fun `searchPlaylists without token returns NotAuthorized` () = runTest {
        assertIs<Result.Error<DataError.Remote.NotAuthorized>>(
            createApiWithoutToken().searchPlaylists(QUERY)
        )
    }

    @Test
    fun `getAudioById without token returns NotAuthorized` () = runTest {
        assertIs<Result.Error<DataError.Remote.NotAuthorized>>(
            createApiWithoutToken().getAudioById(AUDIO_IDS)
        )
    }

    @Test
    fun `searchAlbums without token returns NotAuthorized` () = runTest {
        assertIs<Result.Error<DataError.Remote.NotAuthorized>>(
            createApiWithoutToken().searchAlbums(QUERY)
        )
    }

    @Test
    fun `getPopular without token returns NotAuthorized` () = runTest {
        assertIs<Result.Error<DataError.Remote.NotAuthorized>>(
            createApiWithoutToken().getPopular()
        )
    }

    //POST =====================================================================================
}