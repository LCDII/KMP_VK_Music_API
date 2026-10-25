package dev.lcdii.kmp_vk_music_api.vk_api

import android.content.Context


class AndroidTokenStorage(private val context: Context): TokenStorage{
    override suspend fun saveToken(token: String, userId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getToken(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun getUserId(): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun clearToken() {
        TODO("Not yet implemented")
    }

    override suspend fun replaceToken(token: String, userId: Int) {
        TODO("Not yet implemeanted")
    }
}
