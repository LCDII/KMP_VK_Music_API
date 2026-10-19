package dev.lcdii.kmp_vk_music_api.vk_api

import platform.UIKit.UIViewController

object IosAuth {
    suspend fun authenticate(
        login: String,
        password: String,
        uiViewController: UIViewController
    ) : String {
        return Auth.authenticate(
            login,
            password,
            IosVkCaptchaResolver(uiViewController)
        )
    }
}