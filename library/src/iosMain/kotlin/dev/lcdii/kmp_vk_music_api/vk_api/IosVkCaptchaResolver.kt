package dev.lcdii.kmp_vk_music_api.vk_api

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.UIKit.UIViewController
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKScriptMessage
import platform.WebKit.WKScriptMessageHandlerProtocol
import platform.WebKit.WKUserContentController
import platform.WebKit.WKUserScript
import platform.WebKit.WKUserScriptInjectionTime
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.NSEC_PER_MSEC
import platform.darwin.dispatch_after
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
class IosVkCaptchaResolver(
    private val uiViewController: UIViewController
): VkCaptchaResolver {
    override suspend fun resolve(redirectUri: String): String =
        suspendCancellableCoroutine { continuation ->
            val configuration = WKWebViewConfiguration()
            val userContentController = WKUserContentController()
            val messageHandler = CaptchaMessageHandler()
            userContentController.addScriptMessageHandler(messageHandler, "vkCaptcha")
            userContentController.addUserScript(
                WKUserScript(
                    source = VkCaptchaResolverConfig.SUCCESS_TOKEN_HOOK,
                    injectionTime = WKUserScriptInjectionTime.WKUserScriptInjectionTimeAtDocumentStart,
                    forMainFrameOnly = false
                )
            )
            configuration.userContentController = userContentController

            val webView = WKWebView(
                frame = uiViewController.view.bounds,
                configuration = configuration
            )
            val delegate = CaptchaNavigationDelegate()
            var completed = false

            fun cleanup() {
                webView.stopLoading()
                webView.navigationDelegate = null
                userContentController.removeScriptMessageHandlerForName("vkCaptcha")
                webView.removeFromSuperview()
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

            messageHandler.onToken = ::finish

            fun pollToken() {
                if (completed) return

                webView.evaluateJavaScript("window.__vkSuccessToken || null") { value, _ ->
                    val token = value as? String
                    if (!token.isNullOrBlank()) {
                        finish(token)
                    } else {
                        dispatch_after(
                            dispatch_time(
                                DISPATCH_TIME_NOW,
                                VkCaptchaResolverConfig.POLL_INTERVAL_MILLIS * NSEC_PER_MSEC.toLong()
                            ),
                            dispatch_get_main_queue(),
                            ::pollToken
                        )
                    }
                }
            }

            delegate.onPageFinished = ::pollToken
            webView.navigationDelegate = delegate
            webView.translatesAutoresizingMaskIntoConstraints = false
            uiViewController.view.addSubview(webView)
            uiViewController.view.addConstraints(
                listOf(
                    webView.topAnchor.constraintEqualToAnchor(uiViewController.view.topAnchor),
                    webView.bottomAnchor.constraintEqualToAnchor(uiViewController.view.bottomAnchor),
                    webView.leadingAnchor.constraintEqualToAnchor(uiViewController.view.leadingAnchor),
                    webView.trailingAnchor.constraintEqualToAnchor(uiViewController.view.trailingAnchor)
                )
            )
            webView.loadRequest(NSURLRequest(NSURL(string = redirectUri)))

            dispatch_after(
                dispatch_time(
                    DISPATCH_TIME_NOW,
                    VkCaptchaResolverConfig.CAPTCHA_TIMEOUT_MILLIS * NSEC_PER_MSEC.toLong()
                ),
                dispatch_get_main_queue()
            ) {
                fail(IllegalStateException("Timed out waiting for VK CAPTCHA success_token"))
            }

            continuation.invokeOnCancellation {
                dispatch_after(
                    dispatch_time(DISPATCH_TIME_NOW, 0),
                    dispatch_get_main_queue(),
                    ::cleanup
                )
            }
        }

    private class CaptchaNavigationDelegate : NSObject(), WKNavigationDelegateProtocol {
        var onPageFinished: (() -> Unit)? = null

        override fun webView(
            webView: WKWebView,
            didFinishNavigation: WKNavigation?
        ) {
            onPageFinished?.invoke()
        }
    }

    private class CaptchaMessageHandler : NSObject(), WKScriptMessageHandlerProtocol {
        var onToken: ((String) -> Unit)? = null

        override fun userContentController(
            userContentController: WKUserContentController,
            didReceiveScriptMessage: WKScriptMessage
        ) {
            (didReceiveScriptMessage.body as? String)?.let { onToken?.invoke(it) }
        }
    }

}
