package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.vk_api.core.DataError
import dev.lcdii.kmp_vk_music_api.vk_api.core.Result
import org.koin.mp.KoinPlatformTools

class JvmAuth(
    private val apiSource: ApiSource
) {
    suspend fun auth(
        login: String,
        password: String
    ): Result<ApiResponse, DataError.Remote> {
        return apiSource.auth(
            login = login,
            password = password,
            captchaResolver = JvmVkCaptchaResolver,
            validationResolver = ConsoleVkValidationResolver
        )
    }
}
