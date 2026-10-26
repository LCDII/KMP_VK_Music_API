package dev.lcdii.kmp_vk_music_api.vk_api.data

import dev.lcdii.kmp_vk_music_api.vk_api.domain.TokenStorage
import eu.anifantakis.lib.ksafe.KSafe

internal class KSafeTokenStorage(
    private val ksafe: KSafe
): TokenStorage {
    override suspend fun saveToken(token: String, userId: Int) {
        ksafe.put("access_token", token)
        ksafe.put("user_id", userId)
    }

    override suspend fun getToken(): String? {
        return ksafe.get("access_token", null as String?)
    }

    override suspend fun getUserId(): Int? {
        return ksafe.get("user_id", null as Int?)
    }

    override suspend fun clearToken() {
        ksafe.delete("access_token")
        ksafe.delete("user_id")
    }

    override suspend fun replaceToken(token: String, userId: Int) {
        saveToken(token, userId)
    }
}