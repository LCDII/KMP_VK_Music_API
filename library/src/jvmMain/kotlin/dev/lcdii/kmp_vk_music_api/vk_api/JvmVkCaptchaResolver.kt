package dev.lcdii.kmp_vk_music_api.vk_api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

object JvmVkCaptchaResolver: VkCaptchaResolver {
    override suspend fun resolve(redirectUri: String): String = withContext(Dispatchers.IO) {
        val options = ChromeOptions()
        val driver = ChromeDriver(options)

        try {
            val javascriptExecutor = driver as JavascriptExecutor

            // Install the hook before loading VK's page so its first requests are captured.
            driver.get("about:blank")
            javascriptExecutor.executeScript(VkCaptchaResolverConfig.SUCCESS_TOKEN_HOOK)
            driver.get(redirectUri)

            val deadline = System.currentTimeMillis() + VkCaptchaResolverConfig.CAPTCHA_TIMEOUT_MILLIS
            while (System.currentTimeMillis() < deadline) {
                val token = javascriptExecutor.executeScript(
                    "return window.__vkSuccessToken || null;"
                ) as? String

                if (!token.isNullOrBlank()) {
                    return@withContext token
                }

                Thread.sleep(VkCaptchaResolverConfig.POLL_INTERVAL_MILLIS)
            }

            error("Timed out waiting for VK CAPTCHA success_token")
        } finally {
            driver.quit()
        }
    }

}
