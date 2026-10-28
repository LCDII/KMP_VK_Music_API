package dev.lcdii.kmp_vk_music_api.vk_api


import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkCaptchaResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.File
import java.time.Duration
import java.util.concurrent.CancellationException

internal class JvmVkCaptchaResolver(
    private val browserPath: String? = null,
): VkCaptchaResolver {

    override suspend fun resolve(redirectUri: String): Result<String, DataError> =
        withContext(Dispatchers.IO) {
            val binary = when (val browserResult = resolveBrowserBinary()) {
                is Result.Success -> browserResult.data
                is Result.Error -> return@withContext browserResult
            }

            val driver = try {
                ChromeDriver(ChromeOptions().apply { setBinary(binary) })
            } catch (_: Throwable) {
                return@withContext Result.Error(DataError.Remote.Unknown)
            }

            try {
                injectInterceptScript(driver)
                driver.get(redirectUri)

                WebDriverWait(driver, Duration.ofSeconds(CAPTCHA_TIMEOUT_SECONDS)).until { currentDriver ->
                    (currentDriver as JavascriptExecutor)
                        .executeScript("return window.__vkSuccessToken || null;") != null
                }

                val token = (driver as JavascriptExecutor)
                    .executeScript("return window.__vkSuccessToken;") as? String

                token?.let { Result.Success(it) }
                    ?: Result.Error(
                        DataError.Remote.CaptchaFailed("Captcha token was not returned")
                    )
            } catch (error: CancellationException) {
                throw error
            } catch (_: Throwable) {
                Result.Error(DataError.Remote.Unknown)
            } finally {
                driver.quit()
            }
    }

    private fun resolveBrowserBinary(): Result<String, DataError.Local> {
        return try {
            val path = browserPath ?: findBrowserBinary() ?: return Result.Error(
                DataError.Local.BrowserNotFound("Chrome-compatible browser was not found")
            )

            normalizeAndValidate(path)
        } catch (error: Throwable) {
            Result.Error(DataError.Local.BrowserNotFound(error.message ?: "Unable to resolve browser"))
        }
    }

    private fun normalizeAndValidate(path: String): Result<String, DataError.Local> {
        val real = if (path.endsWith(".app") && System.getProperty("os.name").lowercase().contains("mac")) {
            val name = File(path).nameWithoutExtension
            "$path/Contents/MacOS/$name"
        } else path

        val f = File(real)
        if (!f.exists() || !f.isFile || !f.canExecute()) {
            return Result.Error(
                DataError.Local.BrowserNotFound(
                    "Invalid browser path: $real (file is missing, not a file, or not executable)"
                )
            )
        }

        return Result.Success(real)
    }

    private fun injectInterceptScript(driver: ChromeDriver) {
        val script = """
            (function() {
                const _originalFetch = window.fetch;
                window.fetch = async function(...args) {
                    const response = await _originalFetch(...args);
                    const clone = response.clone();
                    clone.json().then(data => {
                        if (data && data.response && data.response.success_token) {
                            window.__vkSuccessToken = data.response.success_token;
                        }
                    }).catch(() => {});
                    return response;
                };

                const _originalOpen = XMLHttpRequest.prototype.open;
                const _originalSend = XMLHttpRequest.prototype.send;
                XMLHttpRequest.prototype.open = function(method, url, ...rest) {
                    this.__url = url;
                    return _originalOpen.call(this, method, url, ...rest);
                };
                XMLHttpRequest.prototype.send = function(...args) {
                    this.addEventListener('load', function() {
                        try {
                            const data = JSON.parse(this.responseText);
                            if (data && data.response && data.response.success_token) {
                                window.__vkSuccessToken = data.response.success_token;
                            }
                        } catch(e) {}
                    });
                    return _originalSend.apply(this, args);
                };
            })();
        """.trimIndent()

        driver.executeCdpCommand(
            "Page.addScriptToEvaluateOnNewDocument",
            mapOf("source" to script)
        )
    }
}

private const val CAPTCHA_TIMEOUT_SECONDS = 300L

private fun findBrowserBinary(): String? {
    val os = System.getProperty("os.name").lowercase()
    val home = System.getProperty("user.home")
    val localAppData = System.getenv("LOCALAPPDATA")
    val programFiles = System.getenv("ProgramFiles")
    val programFilesX86 = System.getenv("ProgramFiles(x86)")

    val candidates: List<String?> = when {
        os.contains("win") -> listOf(
            programFiles?.let { "$it\\Google\\Chrome\\Application\\chrome.exe" },
            programFilesX86?.let { "$it\\Google\\Chrome\\Application\\chrome.exe" },
            localAppData?.let { "$it\\Google\\Chrome\\Application\\chrome.exe" },
            programFiles?.let { "$it\\BraveSoftware\\Brave-Browser\\Application\\brave.exe" },
            programFilesX86?.let { "$it\\BraveSoftware\\Brave-Browser\\Application\\brave.exe" },
            localAppData?.let { "$it\\BraveSoftware\\Brave-Browser\\Application\\brave.exe" },
            programFiles?.let { "$it\\Chromium\\Application\\chrome.exe" },
            localAppData?.let { "$it\\Chromium\\Application\\chrome.exe" },
            programFiles?.let { "$it\\Microsoft\\Edge\\Application\\msedge.exe" },
            programFilesX86?.let { "$it\\Microsoft\\Edge\\Application\\msedge.exe" },
        )
        os.contains("mac") || os.contains("darwin") -> listOf(
            "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
            "$home/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
            "/Applications/Brave Browser.app/Contents/MacOS/Brave Browser",
            "$home/Applications/Brave Browser.app/Contents/MacOS/Brave Browser",
            "/Applications/Chromium.app/Contents/MacOS/Chromium",
            "$home/Applications/Chromium.app/Contents/MacOS/Chromium",
            "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge",
            "$home/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge",
        )
        else -> listOf(
            "/usr/bin/google-chrome",
            "/usr/bin/google-chrome-stable",
            "/usr/bin/brave-browser",
            "/usr/bin/brave",
            "/usr/bin/chromium",
            "/usr/bin/chromium-browser",
            "/snap/bin/chromium",
            "/snap/bin/brave",
            "/usr/bin/microsoft-edge",
            "/usr/bin/microsoft-edge-stable",
        )
    }

    return candidates.filterNotNull().firstOrNull { path ->
        File(path).let { it.exists() && it.isFile && it.canExecute() }
    }
}
