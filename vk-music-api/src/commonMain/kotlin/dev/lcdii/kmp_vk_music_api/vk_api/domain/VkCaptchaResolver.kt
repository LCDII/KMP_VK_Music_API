package dev.lcdii.kmp_vk_music_api.vk_api.domain

public interface VkCaptchaResolver {
    public suspend fun resolve(redirectUri: String): String?
}