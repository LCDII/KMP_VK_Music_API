package dev.lcdii.kmp_vk_music_api.vk_api

import android.app.Activity

class AndroidVkCaptchaResolver(
    private val activity: Activity
): VkCaptchaResolver {
    override suspend fun resolve(redirectUri: String): String {
        TODO("Not yet implemented")
    }

}