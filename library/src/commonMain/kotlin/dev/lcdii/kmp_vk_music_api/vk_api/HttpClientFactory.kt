package dev.lcdii.kmp_vk_music_api.vk_api
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(engine: HttpClientEngine): HttpClient {
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

        return HttpClient(engine) {
            install(ContentNegotiation) {
                register(
                    ContentType.Application.Json,
                    VkApiContentConverter(json)
                )
            }
            install(Logging) {
                logger = object : Logger{
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }
            //TODO DefaultRequest
        }
    }
}
