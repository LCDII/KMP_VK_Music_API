package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkCaptchaResolver
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.web.WebView
import javafx.stage.Stage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

public object JvmVkCaptchaResolver : VkCaptchaResolver {

    override suspend fun resolve(redirectUri: String): String? {
        try {
            Platform.startup {  }
        } catch (e: IllegalStateException) {

        }
        return suspendCancellableCoroutine { continuation ->
            Platform.runLater {
                val webView = WebView()
                val stage = Stage()

                webView.engine.locationProperty().addListener { _, _, newUrl ->
                    extractSuccessToken(newUrl)?.let { token ->
                        if (continuation.isActive) {
                            continuation.resume(token)
                            stage.close()
                        }
                    }
                }


                webView.engine.load(redirectUri)

                stage.scene = Scene(webView, 400.0, 600.0)
                stage.title = "VK Captcha"
                stage.show()

                stage.setOnCloseRequest {
                    if (continuation.isActive)
                        continuation.resume(null)
                }
            }
        }
    }

    private fun extractSuccessToken(url: String): String? {
        return url.substringAfter("success_token=", "")
            .substringBefore("&")
            .takeIf { it.isNotBlank() }
    }
}
