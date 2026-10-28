package dev.lcdii.kmp_vk_music_api.vk_api

import android.net.Uri
import com.vk.id.captcha.api.VKCaptcha
import com.vk.id.captcha.api.data.VKCaptchaResult
import com.vk.id.captcha.api.listener.VKCaptchaResultListener
import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkCaptchaResolver
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal object AndroidVkIdCaptchaResolver: VkCaptchaResolver { //TODO make async
    override suspend fun resolve(redirectUri: String): Result<String, DataError> =
        suspendCancellableCoroutine { continuation ->
            val listener = object : VKCaptchaResultListener {
                override fun onResult(result: VKCaptchaResult) {
                    when (result) {
                        is VKCaptchaResult.Success -> {
                            if (continuation.isActive) {
                                continuation.resume(Result.Success(result.token))
                            }
                        }
                        is VKCaptchaResult.Error -> {
                            if (continuation.isActive) {
                                continuation.resume(
                                    Result.Error(DataError.Remote.CaptchaFailed("VK ID captcha failed"))
                                )
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
