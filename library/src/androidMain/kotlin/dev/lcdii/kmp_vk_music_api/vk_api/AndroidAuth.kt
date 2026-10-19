package dev.lcdii.kmp_vk_music_api.vk_api

import android.app.Activity

object AndroidAuth {
    suspend fun authenticate(
        activity: Activity,
        login: String,
        password: String
    ) : String{
        return Auth.authenticate(
            login,
            password,
            captchaResolver = AndroidVkCaptchaResolver(activity)
        )
    }
}