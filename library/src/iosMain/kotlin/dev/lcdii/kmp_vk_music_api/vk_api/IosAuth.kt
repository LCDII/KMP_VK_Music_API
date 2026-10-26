package dev.lcdii.kmp_vk_music_api.vk_api

import platform.UIKit.UIViewController
import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.vk_api.domain.ApiResponse
import dev.lcdii.kmp_vk_music_api.vk_api.domain.ApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkCaptchaResolver
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkValidationResolver

public class IosAuth(
    private val apiSource: ApiSource
) {
    public suspend fun auth(
        uiViewController: UIViewController,
        login: String,
        password: String,
        captchaResolver: VkCaptchaResolver? = null,
        validationResolver: VkValidationResolver? = null
    ): Result<ApiResponse, DataError.Remote> =apiSource.auth(
            login = login,
            password = password,
            captchaResolver = captchaResolver ?: IosVkCaptchaResolver(uiViewController),
            validationResolver = validationResolver ?: IosVkValidationResolver
        )
}
