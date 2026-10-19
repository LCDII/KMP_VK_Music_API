package dev.lcdii.kmp_vk_music_api.vk_api

class VkApiService(
    private val audioRepository: AudioRepository,
    private val authRepository: AuthRepository
): ApiService {
}