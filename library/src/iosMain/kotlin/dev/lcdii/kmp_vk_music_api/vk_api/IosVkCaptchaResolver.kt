package dev.lcdii.kmp_vk_music_api.vk_api

import platform.UIKit.UIViewController

class IosVkCaptchaResolver(
    private val uiViewController: UIViewController
): VkCaptchaResolver {
    override suspend fun resolve(redirectUri: String): String {
        TODO("Not yet implemented")
    }
}