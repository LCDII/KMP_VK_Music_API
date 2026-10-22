package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.vk_api.core.DataError
import dev.lcdii.kmp_vk_music_api.vk_api.core.Result

interface ApiSource {
    suspend fun authenticate(
        login: String,
        password: String,
        captchaResolver: VkCaptchaResolver
    ): Result<ApiResponse, DataError.Remote>
    suspend fun getProfileInfo(): Result<ApiResponse, DataError.Remote>
    suspend fun getUsersAudioCount(userId: Int): Result<ApiResponse, DataError.Remote>
    suspend fun getUsersAudio(
        userId: Int,
        count: Int? = null,
        offset: Int? = null,
        playlistAttributes: PlaylistAttributes? = null
    ): Result<ApiResponse, DataError.Remote>
    suspend fun getUsersPlaylist(
        userId: Int,
        count: Int? = null,
        offset: Int?= null
    ): Result<ApiResponse, DataError.Remote>
    suspend fun searchAudio(
        query: String,
        count: Int? = null,
        offset: Int? = null
    ): Result<ApiResponse, DataError.Remote>
    suspend fun searchPlaylists(
        query: String,
        count: Int? = null,
        offset: Int? = null
    ): Result<ApiResponse, DataError.Remote>
}
