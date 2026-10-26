package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkCaptchaResolver
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkCaptchaResolverConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

internal object JvmVkCaptchaResolver: VkCaptchaResolver {
    override suspend fun resolve(redirectUri: String): String = withContext(Dispatchers.IO) {
        val options = ChromeOptions().apply {
            setBinary(System.getProperty("vk.brave.binary", BRAVE_BINARY))
            setPageLoadStrategy(PageLoadStrategy.EAGER)
            addArguments("--no-first-run")
            addArguments("--no-default-browser-check")
            addArguments("--disable-dev-shm-usage")
        }
        val driver = ChromeDriver(options)

        try {
            val javascriptExecutor = driver as JavascriptExecutor

            driver.get(redirectUri)
            // A navigation destroys the previous JavaScript context, so install the hook
            // in the actual CAPTCHA page before waiting for the user's interaction.
            javascriptExecutor.executeScript(VkCaptchaResolverConfig.SUCCESS_TOKEN_HOOK)

            val deadline = System.currentTimeMillis() + VkCaptchaResolverConfig.CAPTCHA_TIMEOUT_MILLIS
            while (System.currentTimeMillis() < deadline) {
                // VK may reload the CAPTCHA page after the user confirms it.
                javascriptExecutor.executeScript(VkCaptchaResolverConfig.SUCCESS_TOKEN_HOOK)
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

    private const val BRAVE_BINARY = "/usr/bin/brave"

}
