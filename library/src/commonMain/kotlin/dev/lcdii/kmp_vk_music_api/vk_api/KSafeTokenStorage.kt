package dev.lcdii.kmp_vk_music_api.vk_api

import eu.anifantakis.lib.ksafe.KSafe

class KSafeTokenStorage(
    private val ksafe: KSafe
): TokenStorage {
    override fun saveToken(token: String, userId: Int) {
        ksafe.putDirect("access_token", token)
        ksafe.putDirect("user_id", userId)
    }

    override fun getToken(): String? {
        return ksafe.getDirect<String?>(
            key = "access_token",
            defaultValue = null
        )
    }

    override fun getUserId(): Int? {
        return ksafe.getDirect<Int?>(
            key = "user_id",
            defaultValue = null
        )
    }

    override fun clearToken() {
        ksafe.deleteDirect("access_token")
        ksafe.deleteDirect("user_id")
    }

    override fun replaceToken(token: String, userId: Int) {
        saveToken(token, userId)
    }
}
