package dev.lcdii.kmp_vk_music_api.vk_api.data

import dev.lcdii.kmp_vk_music_api.vk_api.components.BASE_URL
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_EXTENDED
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_HTTPS
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_LANG
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_V
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging

object HttpClientFactory {
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
