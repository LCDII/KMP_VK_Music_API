package dev.lcdii.kmp_vk_music_api

import com.goncalossilva.resources.Resource
import dev.lcdii.kmp_vk_music_api.vk_api.HttpClientFactory
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiRequestBuilder
import dev.lcdii.kmp_vk_music_api.vk_api.components.AUDIO_COUNT_URL
import dev.lcdii.kmp_vk_music_api.vk_api.components.BASE_URL
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class HttpClientTest {
    @Test
    fun test1() = runBlocking {
        val mockEngine= MockEngine { request ->
            assertEquals(123, request.url.parameters["owner_id"]?.toInt())

            respond(
                content = Resource("json_responses/audio/get_count.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.ContentType,
                    "application/json"
                )
            )
        }

        val client = HttpClientFactory.create(mockEngine)

        val userId = 123
        val request = VkApiRequestBuilder.buildGetUsersAudiosCountRequest(userId)

        val response = client.get(request.url) {
            for(h in request.headers) {
                header(h.key, h.value)
            }
            parameter("https", request.params.https)
            parameter("lang", request.params.lang)
            parameter("extended", request.params.extended)
            parameter("v", request.params.v)

            for(p in request.params.other) {
                parameter(p.key, p.value)
            }
        }

    }
}