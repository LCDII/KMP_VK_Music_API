package dev.lcdii.kmp_vk_music_api.vk_api

import platform.UIKit.UIViewController
import dev.lcdii.kmp_vk_music_api.vk_api.core.DataError
import dev.lcdii.kmp_vk_music_api.vk_api.core.Result
import org.koin.mp.KoinPlatformTools

object IosAuth {
    suspend fun auth(
        login: String,
        password: String,
        uiViewController: UIViewController
    ): Result<ApiResponse, DataError.Remote> {
        return KoinPlatformTools.defaultContext().get().get<ApiSource>().auth(
            login = login,
            password = password,
            captchaResolver = IosVkCaptchaResolver(uiViewController),
            validationResolver = IosVkValidationResolver
        )
    }
}
