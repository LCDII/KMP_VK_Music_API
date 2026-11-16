package dev.lcdii.kmp_vk_music_api.vk_api.data

import dev.lcdii.kmp_vk_music_api.core.ACCESS_TOKEN_KEY
import dev.lcdii.kmp_vk_music_api.vk_api.domain.TokenStorage
import eu.anifantakis.lib.ksafe.KSafe

internal class KSafeTokenStorage(
    private val ksafe: KSafe
): TokenStorage {
    override suspend fun saveToken(token: String) {
        ksafe.put(ACCESS_TOKEN_KEY, token)
    }

    override suspend fun getToken(): String? {
        return ksafe.get(ACCESS_TOKEN_KEY, null as String?)
    }

    override suspend fun clearToken() {
        ksafe.delete(ACCESS_TOKEN_KEY)
    }

    override suspend fun replaceToken(token: String) {
        saveToken(token)
    }
}