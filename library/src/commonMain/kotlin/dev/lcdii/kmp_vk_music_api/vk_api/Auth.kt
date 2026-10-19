package dev.lcdii.kmp_vk_music_api.vk_api

object Auth {
    suspend fun authenticate(
        login: String,
        password: String,
        captchaResolver: VkCaptchaResolver,
        ): String {
        //TODO
        captchaResolver.resolve("")
        return ""
    }
}
