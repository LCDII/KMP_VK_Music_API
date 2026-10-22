package dev.lcdii.kmp_vk_music_api.vk_api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

class JvmVkCaptchaResolver: VkCaptchaResolver {
    override suspend fun resolve(redirectUri: String): String = withContext(Dispatchers.IO) {
        val options = ChromeOptions()
        val driver = ChromeDriver(options)

        try {
            val javascriptExecutor = driver as JavascriptExecutor

            // Install the hook before loading VK's page so its first requests are captured.
            driver.get("about:blank")
            javascriptExecutor.executeScript(SUCCESS_TOKEN_HOOK)
            driver.get(redirectUri)

            val deadline = System.currentTimeMillis() + CAPTCHA_TIMEOUT_MILLIS
            while (System.currentTimeMillis() < deadline) {
                val token = javascriptExecutor.executeScript(
                    "return window.__vkSuccessToken || null;"
                ) as? String

                if (!token.isNullOrBlank()) {
                    return@withContext token
                }

                Thread.sleep(POLL_INTERVAL_MILLIS)
            }

            error("Timed out waiting for VK CAPTCHA success_token")
        } finally {
            driver.quit()
        }
    }

    private companion object {
        const val POLL_INTERVAL_MILLIS = 500L
        const val CAPTCHA_TIMEOUT_MILLIS = 5 * 60 * 1000L

        const val SUCCESS_TOKEN_HOOK = """
            (() => {
                window.__vkSuccessToken = null;

                const saveToken = (data) => {
                    if (data && data.response && data.response.success_token) {
                        window.__vkSuccessToken = data.response.success_token;
                    }
                };

                const originalFetch = window.fetch;
                window.fetch = async function(...args) {
                    const response = await originalFetch.apply(this, args);
                    response.clone().json().then(saveToken).catch(() => {});
                    return response;
                };

                const originalOpen = XMLHttpRequest.prototype.open;
                const originalSend = XMLHttpRequest.prototype.send;

                XMLHttpRequest.prototype.open = function(...args) {
                    this.__vkUrl = args[1];
                    return originalOpen.apply(this, args);
                };

                XMLHttpRequest.prototype.send = function(...args) {
                    this.addEventListener('load', function() {
                        try {
                            saveToken(JSON.parse(this.responseText));
                        } catch (_) {}
                    });
                    return originalSend.apply(this, args);
                };
            })();
        """
    }
}
