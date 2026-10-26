package dev.lcdii.kmp_vk_music_api.vk_api.domain

import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.PlaylistAttributes

public interface ApiSource {
    public suspend fun auth(
        login: String,
        password: String,
        captchaResolver: VkCaptchaResolver,
        validationResolver: VkValidationResolver
    ): Result<ApiResponse, DataError.Remote>
    public suspend fun getUserId(): Result<Int, DataError.Remote>
    public suspend fun getProfileInfo(): Result<ApiResponse, DataError.Remote>
    public suspend fun getUsersAudioCount(userId: Int): Result<ApiResponse, DataError.Remote>
    public suspend fun getUsersAudio(
        userId: Int,
        count: Int? = null,
        offset: Int? = null,
        playlistAttributes: PlaylistAttributes? = null
    ): Result<ApiResponse, DataError.Remote>
    public suspend fun getUsersPlaylist(
        userId: Int,
        count: Int? = null,
        offset: Int?= null
    ): Result<ApiResponse, DataError.Remote>
    public suspend fun searchAudio(
        query: String,
        count: Int? = null,
        offset: Int? = null
    ): Result<ApiResponse, DataError.Remote>
    public suspend fun searchPlaylists(
        query: String,
        count: Int? = null,
        offset: Int? = null
    ): Result<ApiResponse, DataError.Remote>
}