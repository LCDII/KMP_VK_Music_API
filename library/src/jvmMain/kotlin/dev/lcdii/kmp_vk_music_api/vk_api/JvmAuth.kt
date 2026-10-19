package dev.lcdii.kmp_vk_music_api.vk_api

object JvmAuth {
    suspend fun authenticate(
        login: String,
        password: String
    ) : String {
        return Auth.authenticate(
            login,
            password,
            JvmVkCaptchaResolver()
        )
    }
}