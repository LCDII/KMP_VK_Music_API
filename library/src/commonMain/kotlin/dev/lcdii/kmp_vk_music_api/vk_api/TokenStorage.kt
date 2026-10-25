package dev.lcdii.kmp_vk_music_api.vk_api

interface TokenStorage {
    suspend fun saveToken(token: String, userId: Int)
    suspend fun getToken(): String?
    suspend fun getUserId(): Int?
    suspend fun clearToken()
    suspend fun replaceToken(token: String, userId: Int)
}
