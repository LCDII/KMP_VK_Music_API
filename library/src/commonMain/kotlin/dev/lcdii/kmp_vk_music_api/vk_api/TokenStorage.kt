package dev.lcdii.kmp_vk_music_api.vk_api

interface TokenStorage {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
    fun replaceToken(token: String)
}
