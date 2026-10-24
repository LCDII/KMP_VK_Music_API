package dev.lcdii.kmp_vk_music_api.vk_api

interface TokenStorage {
    fun saveToken(token: String, userId: Int)
    fun getToken(): String?
    fun getUserId(): Int?
    fun clearToken()
    fun replaceToken(token: String, userId: Int)
}
