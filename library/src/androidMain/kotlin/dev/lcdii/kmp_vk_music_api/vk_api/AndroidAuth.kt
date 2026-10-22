package dev.lcdii.kmp_vk_music_api.vk_api

import android.app.Activity
import dev.lcdii.kmp_vk_music_api.vk_api.core.DataError
import dev.lcdii.kmp_vk_music_api.vk_api.core.Result
import org.koin.mp.KoinPlatformTools

object AndroidAuth {
    suspend fun authenticate(
        activity: Activity,
        login: String,
        password: String
    ): Result<ApiResponse, DataError.Remote> {
        return KoinPlatformTools.defaultContext().get().get<ApiSource>().authenticate(
            login = login,
            password = password,
            captchaResolver = AndroidVkCaptchaResolver(activity),
            validationResolver = AndroidVkValidationResolver(activity)
        )
    }
}
