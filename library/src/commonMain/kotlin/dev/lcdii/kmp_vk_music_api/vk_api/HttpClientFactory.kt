package dev.lcdii.kmp_vk_music_api.vk_api
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine

object HttpClientFactory {
    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine)
    }
}