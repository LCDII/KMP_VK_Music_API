package dev.lcdii.kmp_vk_music_api.vk_api

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONTokener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AndroidVkCaptchaResolver(
    private val activity: Activity
): VkCaptchaResolver {
    override suspend fun resolve(redirectUri: String): String =
        suspendCancellableCoroutine { continuation ->
            activity.runOnUiThread {
                val webView = WebView(activity)
                val handler = Handler(Looper.getMainLooper())
                var completed = false

                fun cleanup() {
                    handler.removeCallbacksAndMessages(null)
                    webView.stopLoading()
                    (webView.parent as? ViewGroup)?.removeView(webView)
                    webView.destroy()
                }

                fun fail(error: Throwable) {
                    if (completed) return
                    completed = true
                    cleanup()
                    continuation.resumeWithException(error)
                }

                fun finish(token: String) {
                    if (completed) return
                    completed = true
                    cleanup()
                    continuation.resume(token)
                }

                fun pollToken() {
                    if (completed) return

                    webView.evaluateJavascript("window.__vkSuccessToken || null;") { value ->
                        val token = runCatching {
                            JSONTokener(value).nextValue() as? String
                        }.getOrNull()

                        if (!token.isNullOrBlank()) {
                            finish(token)
                        } else {
                            handler.postDelayed(
                                ::pollToken,
                                VkCaptchaResolverConfig.POLL_INTERVAL_MILLIS
                            )
                        }
                    }
                }

                webView.settings.javaScriptEnabled = true
                webView.settings.domStorageEnabled = true
                webView.webViewClient = object : WebViewClient() {
                    override fun onPageStarted(
                        view: WebView,
                        url: String?,
                        favicon: android.graphics.Bitmap?
                    ) {
                        view.evaluateJavascript(VkCaptchaResolverConfig.SUCCESS_TOKEN_HOOK, null)
                    }

                    override fun onPageFinished(view: WebView, url: String?) {
                        view.evaluateJavascript(VkCaptchaResolverConfig.SUCCESS_TOKEN_HOOK, null)
                        pollToken()
                    }
                }

                webView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                activity.addContentView(webView, webView.layoutParams)
                webView.loadUrl(redirectUri)
                handler.postDelayed(
                    { fail(IllegalStateException("Timed out waiting for VK CAPTCHA success_token")) },
                    VkCaptchaResolverConfig.CAPTCHA_TIMEOUT_MILLIS
                )

                continuation.invokeOnCancellation {
                    activity.runOnUiThread(::cleanup)
                }
            }
        }

}
