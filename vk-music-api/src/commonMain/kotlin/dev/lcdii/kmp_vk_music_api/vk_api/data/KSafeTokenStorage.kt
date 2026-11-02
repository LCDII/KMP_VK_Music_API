package dev.lcdii.kmp_vk_music_api.vk_api.data

import dev.lcdii.kmp_vk_music_api.core.ACCESS_TOKEN_KEY
import dev.lcdii.kmp_vk_music_api.vk_api.domain.TokenStorage
import eu.anifantakis.lib.ksafe.KSafe

private const val USER_ID_KEY = "user_id"

internal class KSafeTokenStorage(
    private val ksafe: KSafe
): TokenStorage {
    override suspend fun saveToken(token: String, userId: Int) {
        ksafe.put(ACCESS_TOKEN_KEY, token)
        ksafe.put(USER_ID_KEY, userId)
    }

    override suspend fun getToken(): String? {
        return ksafe.get(ACCESS_TOKEN_KEY, null as String?)
    }

    override suspend fun getUserId(): Int? {
        return ksafe.get(USER_ID_KEY, null as Int?)
    }

    override suspend fun clearToken() {
        ksafe.delete(ACCESS_TOKEN_KEY)
        ksafe.delete(USER_ID_KEY)
    }

    override suspend fun replaceToken(token: String, userId: Int) {
        saveToken(token, userId)
    }
}