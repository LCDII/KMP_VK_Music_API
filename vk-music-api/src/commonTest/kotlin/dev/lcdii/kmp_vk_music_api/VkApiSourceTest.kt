package dev.lcdii.kmp_vk_music_api

import com.goncalossilva.resources.Resource
import dev.lcdii.kmp_vk_music_api.core.ACCESS_TOKEN_KEY
import dev.lcdii.kmp_vk_music_api.core.BASE_URL
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_EXTENDED
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_HTTPS
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_LANG
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_V
import dev.lcdii.kmp_vk_music_api.core.PROFILE_INFO_URL
import dev.lcdii.kmp_vk_music_api.core.USER_AGENT_KEY
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.data.HttpClientFactory
import dev.lcdii.kmp_vk_music_api.vk_api.data.VkMobileAppClient
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

private const val TEST_TOKEN = "token"

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
                content = Resource("json_responses/account/get_profile_info.json").toString(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClientFactory.create(engine)
        val api = VkApiSource(
            httpClient = httpClient,
            tokenStorage = TestTokenStorage()
        )
        api.getProfileInfo()
    }
}