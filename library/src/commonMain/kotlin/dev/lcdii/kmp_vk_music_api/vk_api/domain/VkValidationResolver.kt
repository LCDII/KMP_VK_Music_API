package dev.lcdii.kmp_vk_music_api.vk_api.domain

import dev.lcdii.kmp_vk_music_api.core.VkApiError

public interface VkValidationResolver {
    public suspend fun resolve(error: VkApiError.TextError): String
}