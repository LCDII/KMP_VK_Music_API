package dev.lcdii.kmp_vk_music_api.vk_api

import eu.anifantakis.lib.ksafe.KSafe

class KSafeTokenStorage(
    private val ksafe: KSafe
): TokenStorage {
    override fun saveToken(token: String) {
        ksafe.putDirect("access_token", token)
    }

    override fun getToken(): String? {
        return ksafe.getDirect<String?>(
            key = "access_token",
            defaultValue = null
        )
    }

    override fun clearToken() {
        ksafe.deleteDirect("access_token")
    }

    override fun replaceToken(token: String) {
        saveToken(token)
    }
}