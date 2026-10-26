package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.vk_api.domain.ApiResponse
import dev.lcdii.kmp_vk_music_api.vk_api.domain.ApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkCaptchaResolver
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkValidationResolver

public class JvmAuth(
    private val apiSource: ApiSource
) {
    public suspend fun auth(
        login: String,
        password: String,
        captchaResolver: VkCaptchaResolver? = null,
        validationResolver: VkValidationResolver? = null
    ): Result<ApiResponse, DataError.Remote> {
        return apiSource.auth(
            login = login,
            password = password,
            captchaResolver = captchaResolver ?: JvmVkCaptchaResolver,
            validationResolver = validationResolver ?: ConsoleVkValidationResolver
        )
    }
}
