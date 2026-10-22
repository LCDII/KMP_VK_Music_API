package dev.lcdii.kmp_vk_music_api.vk_api

interface VkCaptchaResolver {
    suspend fun resolve(redirectUri: String): String
}

internal object VkCaptchaResolverConfig {
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
