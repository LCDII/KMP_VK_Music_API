package dev.lcdii.kmp_vk_music_api.vk_api.domain

import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.Result

public interface VkCaptchaResolver {
    public suspend fun resolve(redirectUri: String): Result<String, DataError>
}