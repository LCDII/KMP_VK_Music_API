package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.core.VkApiError
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkValidationResolver

internal object IosVkValidationResolver : VkValidationResolver {
    override suspend fun resolve(error: VkApiError.TextError): String {
        TODO()
    }
}
