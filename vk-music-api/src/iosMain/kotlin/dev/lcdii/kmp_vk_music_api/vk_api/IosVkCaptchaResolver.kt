package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkCaptchaResolver
import platform.UIKit.UIViewController

internal class IosVkCaptchaResolver(
    private val uiViewController: UIViewController
): VkCaptchaResolver {
    override suspend fun resolve(redirectUri: String): Result<String, DataError> =
        Result.Error(DataError.Remote.CaptchaFailed("iOS captcha resolver is not implemented"))
}
