package dev.lcdii.kmp_vk_music_api.vk_api.domain

public interface TokenStorage {
    public suspend fun saveToken(token: String, userId: Int)
    public suspend fun getToken(): String?
    public suspend fun getUserId(): Int?
    public suspend fun clearToken()
    public suspend fun replaceToken(token: String, userId: Int)
}