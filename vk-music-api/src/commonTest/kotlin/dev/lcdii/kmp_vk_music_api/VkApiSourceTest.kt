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
import dev.lcdii.kmp_vk_music_api.vk_api.data.VkMobileAppClient
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.VkApiResponse
import dev.lcdii.kmp_vk_music_api.vk_api.domain.AppClient
import dev.lcdii.kmp_vk_music_api.vk_api.domain.PlaylistAttributes
import dev.lcdii.kmp_vk_music_api.vk_api.domain.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import kotlinx.serialization.json.Json
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
            error("HTTP request must not be sent without token")
        }
        return VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestEmptyTokenStorage()
        )
    }

    private object TestHttpClientFactory {
        fun create(engine: HttpClientEngine): HttpClient =
            HttpClient(engine) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            println(message)
                        }
                    }
                    level = LogLevel.ALL
                }
                defaultRequest {
                    url(BASE_URL)
                    url.parameters.append("https", DEFAULT_HTTPS.toString())
                    url.parameters.append("lang", DEFAULT_LANG)
                    url.parameters.append("extended", DEFAULT_EXTENDED.toString())
                    url.parameters.append("v", DEFAULT_V)
                }
            }
    }

    private data object TestAppClient: AppClient {
        override val userAgent = "VKAndroidApp/4.13.1-1206 (Android 4.4.3; SDK 19; armeabi; ; ru)"
        override val clientId = "2274003"
        override val clientSecret = "hHbZxrka2uZ6jB1inYsH"
    }

    private val testJson = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private fun assertEqualsStandardGetRequestParameters(request: HttpRequestData) {
        //standard
        assertEquals(HttpMethod.Get, request.method)
        assertEquals(Url(BASE_URL).host, request.url.host)
        assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
        assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
        assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
        assertEquals(DEFAULT_V,request.url.parameters["v"])
        assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
        assertEquals(TEST_TOKEN, request.url.parameters[ACCESS_TOKEN_KEY])
    }

    private fun assertEqualsStandardPostRequestParameters(request: HttpRequestData) {
        //standard
        assertEquals(HttpMethod.Post, request.method)
        assertEquals(Url(BASE_URL).host, request.url.host)
        assertEquals(DEFAULT_HTTPS.toString(),request.url.parameters["https"])
        assertEquals(DEFAULT_LANG,request.url.parameters["lang"])
        assertEquals(DEFAULT_EXTENDED.toString(),request.url.parameters["extended"])
        assertEquals(DEFAULT_V,request.url.parameters["v"])
        assertEquals(VkMobileAppClient.userAgent, request.headers[USER_AGENT_KEY])
    }

    private fun createTestGetEngine(
        expectedPath: String,
        expectedUrlParameters: Map<String, String> = emptyMap(),
        testJsonsPath: String
    ): MockEngine = MockEngine { request ->
        assertEqualsStandardGetRequestParameters(request)
        assertEquals("/method/${expectedPath}",request.url.encodedPath)
        for ((key, value) in expectedUrlParameters) {
            assertEquals(value, request.url.parameters[key])
        }
        respond(
            content = Resource("json_responses/${testJsonsPath}").readText(),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }

    private fun createTestPostEngine(
        expectedPath: String,
        expectedFormParameters: Map<String, String>,
        testJsonsPath: String
    ): MockEngine = MockEngine { request ->
        assertEqualsStandardPostRequestParameters(request)
        assertEquals("/method/$expectedPath", request.url.encodedPath)

        val body = assertIs<FormDataContent>(request.body)

        assertEquals(
            ContentType.Application.FormUrlEncoded,
            body.contentType.withoutParameters()
        )//application/x-www-form-urlencoded
        assertEquals(TEST_TOKEN, body.formData[ACCESS_TOKEN_KEY])

        for ((key, value) in expectedFormParameters) {
            assertEquals(value, body.formData[key])
        }

        respond(
            content = Resource("json_responses/$testJsonsPath").readText(),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }

    //GET ==========================================================================================
    @Test
    fun `getProfileInfo sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = PROFILE_INFO_URL,
            testJsonsPath = "account/get_profile_info.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.ProfileInfo>>(api.getProfileInfo())
    }

    @Test
    fun `getUsersAudioCount sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_COUNT_URL,
            expectedUrlParameters = mapOf(
                "owner_id" to USER_ID.toString()
            ),
            testJsonsPath = "audio/get_count.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGetCount>>(
            api.getUsersAudioCount(USER_ID)
        )
    }

    @Test
    fun `getUsersAudio sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_GET_URL,
            expectedUrlParameters = mapOf(
                "owner_id" to USER_ID.toString(),
                "count" to DEFAULT_AUDIO_COUNT.toString(),
                "offset" to DEFAULT_AUDIO_OFFSET.toString()
            ),
            testJsonsPath = "audio/get.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGet>>(api.getUsersAudio(USER_ID))
    }

    @Test
    fun `getUsersAudio with custom count and offset sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_GET_URL,
            expectedUrlParameters = mapOf(
                "owner_id" to USER_ID.toString(),
                "count" to AUDIO_COUNT.toString(),
                "offset" to AUDIO_OFFSET.toString()
            ),
            testJsonsPath = "audio/get.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGet>>(
            api.getUsersAudio(USER_ID, AUDIO_COUNT, AUDIO_OFFSET)
        )
    }

    @Test
    fun `getUsersAudio from playlist sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_GET_URL,
            expectedUrlParameters = mapOf(
                "owner_id" to USER_ID.toString(),
                "count" to DEFAULT_AUDIO_COUNT.toString(),
                "offset" to DEFAULT_AUDIO_OFFSET.toString(),
                "album_id" to PLAYLIST_ID.toString(),
                "access_key" to ACCESS_KEY
            ),
            testJsonsPath = "audio/get.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGet>>(
            api.getUsersAudio(
                userId = USER_ID,
                playlistAttributes = PlaylistAttributes(PLAYLIST_ID, ACCESS_KEY)
            )
        )
    }

    @Test
    fun `getUsersPlaylists sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_PLAYLIST_GET_URL,
            expectedUrlParameters = mapOf(
                "owner_id" to USER_ID.toString(),
                "count" to DEFAULT_PLAYLIST_COUNT.toString(),
                "offset" to DEFAULT_PLAYLIST_OFFSET.toString()
            ),
            testJsonsPath = "audio/get_playlists.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGetPlayLists>>(
            api.getUsersPlaylists(USER_ID)
        )
    }

    @Test
    fun `getUsersPlaylists with custom count and offset sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_PLAYLIST_GET_URL,
            expectedUrlParameters = mapOf(
                "owner_id" to USER_ID.toString(),
                "count" to PLAYLIST_COUNT.toString(),
                "offset" to PLAYLIST_OFFSET.toString()
            ),
            testJsonsPath = "audio/get_playlists.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGetPlayLists>>(
            api.getUsersPlaylists(USER_ID, PLAYLIST_COUNT, PLAYLIST_OFFSET)
        )
    }

    @Test
    fun `searchAudio sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_SEARCH_URL,
            expectedUrlParameters = mapOf(
                "q" to QUERY,
                "count" to DEFAULT_AUDIO_COUNT.toString(),
                "offset" to DEFAULT_AUDIO_OFFSET.toString(),
                "autocomplete" to DEFAULT_AUDIO_AUTOCOMPLETE_MODE.toString(),
                "sort" to DEFAULT_AUDIO_SORT_MODE.toString()
            ),
            testJsonsPath = "audio/search.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioSearch>>(api.searchAudio(QUERY))
    }

    @Test
    fun `searchAudio with custom offset and count sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_SEARCH_URL,
            expectedUrlParameters = mapOf(
                "q" to QUERY,
                "count" to SEARCH_AUDIO_COUNT.toString(),
                "offset" to SEARCH_AUDIO_OFFSET.toString(),
                "autocomplete" to DEFAULT_AUDIO_AUTOCOMPLETE_MODE.toString(),
                "sort" to DEFAULT_AUDIO_SORT_MODE.toString()
            ),
            testJsonsPath = "audio/search.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioSearch>>(
            api.searchAudio(QUERY, SEARCH_AUDIO_COUNT, SEARCH_AUDIO_OFFSET)
        )
    }

    @Test
    fun `searchPlaylists sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_SEARCH_PLAYLIST_URL,
            expectedUrlParameters = mapOf(
                "q" to QUERY,
                "count" to DEFAULT_PLAYLIST_COUNT.toString(),
                "offset" to DEFAULT_PLAYLIST_OFFSET.toString()
            ),
            testJsonsPath = "audio/search_playlists.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioSearchPlaylists>>(api.searchPlaylists(QUERY))
    }

    @Test
    fun `searchPlaylists with custom offset and count sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_SEARCH_PLAYLIST_URL,
            expectedUrlParameters = mapOf(
                "q" to QUERY,
                "count" to SEARCH_PLAYLIST_COUNT.toString(),
                "offset" to SEARCH_PLAYLIST_OFFSET.toString()
            ),
            testJsonsPath = "audio/search_playlists.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioSearchPlaylists>>(
            api.searchPlaylists(QUERY, SEARCH_PLAYLIST_COUNT, SEARCH_PLAYLIST_OFFSET)
        )
    }

    @Test
    fun `getAudioById sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_GET_BY_ID_URL,
            expectedUrlParameters = mapOf(
                "audios" to AUDIO_IDS.joinToString(",")
            ),
            testJsonsPath = "audio/get_by_id.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGetById>>(
            api.getAudioById(AUDIO_IDS)
        )
    }

    @Test
    fun `getAudioById with empty ids have DataError` () = runTest {
        val engine = MockEngine {
            error("HTTP request must not be sent for empty audio ids")
        }
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Error<DataError.Remote.Api>>(api.getAudioById(emptyList()))
    }

    @Test
    fun `searchAlbums sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_SEARCH_ALBUMS_URL,
            expectedUrlParameters = mapOf(
                "q" to QUERY,
                "count" to DEFAULT_PLAYLIST_COUNT.toString(),
                "offset" to DEFAULT_PLAYLIST_OFFSET.toString()
            ),
            testJsonsPath = "audio/search_albums.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioSearchAlbums>>(api.searchAlbums(QUERY))
    }

    @Test
    fun `searchAlbums with custom offset and count sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_SEARCH_ALBUMS_URL,
            expectedUrlParameters = mapOf(
                "q" to QUERY,
                "count" to SEARCH_ALBUMS_COUNT.toString(),
                "offset" to SEARCH_ALBUMS_OFFSET.toString()
            ),
            testJsonsPath = "audio/search_albums.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioSearchAlbums>>(
            api.searchAlbums(QUERY, SEARCH_ALBUMS_COUNT, SEARCH_ALBUMS_OFFSET)
        )
    }

    @Test
    fun `getPopular sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_GET_POPULAR_URL,
            expectedUrlParameters = mapOf(
                "count" to DEFAULT_AUDIO_COUNT.toString(),
                "offset" to DEFAULT_AUDIO_OFFSET.toString()
            ),
            testJsonsPath = "audio/get_popular.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGetPopular>>(api.getPopular())
    }

    @Test
    fun `getPopular with custom offset and count sends right request` () = runTest {
        val engine = createTestGetEngine(
            expectedPath = AUDIO_GET_POPULAR_URL,
            expectedUrlParameters = mapOf(
                "count" to POPULAR_COUNT.toString(),
                "offset" to POPULAR_OFFSET.toString()
            ),
            testJsonsPath = "audio/get_popular.json"
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = TestTokenStorage()
        )
        assertIs<Result.Success<VkApiResponse.AudioGetPopular>>(
            api.getPopular(POPULAR_COUNT, POPULAR_OFFSET)
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

    @Test
    fun `addAudio sends right form` () = runTest {

    }
}