package dev.lcdii.kmp_vk_music_api.vk_api

import android.net.Uri
import com.vk.id.captcha.api.VKCaptcha
import com.vk.id.captcha.api.data.VKCaptchaResult
import com.vk.id.captcha.api.listener.VKCaptchaResultListener
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkCaptchaResolver
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

public object AndroidVkIdCaptchaResolver: VkCaptchaResolver {
    override suspend fun resolve(redirectUri: String): String? =
        suspendCancellableCoroutine { continuation ->
            val listener = object : VKCaptchaResultListener {
                override fun onResult(result: VKCaptchaResult) {
                    when (result) {
                        is VKCaptchaResult.Success -> {
                            if (continuation.isActive) {
                                continuation.resume(result.token)
                            }
                        }
                        is VKCaptchaResult.Error -> {
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                        }
                    }
                }
            }

            val uri = Uri.parse(redirectUri)
            val domain = "${uri.scheme}://${uri.host}"

            VKCaptcha.openCaptcha(
                domain, redirectUri, listener
            )
            continuation.invokeOnCancellation{
                VKCaptcha.closeCaptcha()
            }
        }
    }
