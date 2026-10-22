package dev.lcdii.kmp_vk_music_api.vk_api

interface VkValidationResolver {
    suspend fun resolve(error: VkApiError.TextError): String {
        error("VK validation resolver is not implemented")
    }
}
