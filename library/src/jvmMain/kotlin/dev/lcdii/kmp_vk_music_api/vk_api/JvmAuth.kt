package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.vk_api.core.DataError
import dev.lcdii.kmp_vk_music_api.vk_api.core.Result
import org.koin.mp.KoinPlatformTools

object JvmAuth {
    suspend fun auth(
        login: String,
        password: String
    ): Result<ApiResponse, DataError.Remote> {
        return KoinPlatformTools.defaultContext().get().get<ApiSource>().auth(
            login = login,
            password = password,
            captchaResolver = JvmVkCaptchaResolver,
            validationResolver = ConsoleVkValidationResolver
        )
    }
}
