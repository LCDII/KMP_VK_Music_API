package dev.lcdii.kmp_vk_music_api.vk_api.domain

import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.Result

public interface ApiSource {
    public suspend fun auth(
        login: String,
        password: String,
        captchaResolver: VkCaptchaResolver,
        validationResolver: VkValidationResolver
    ): Result<ApiResponse, DataError>
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
    public suspend fun addAudio(
        ownerId: Int,
        audioId: Int
    ): Result<ApiResponse, DataError.Remote>
    public suspend fun addAudiosToPlaylist(
        ownerId: Int,
        playlistId: Int,
        audioIds: List<String>
    ): Result<ApiResponse, DataError.Remote>
    public suspend fun deleteAudio(
        ownerId: Int,
        audioId: Int
    ): Result<ApiResponse, DataError.Remote>
    public suspend fun removeAudiosFromPlaylist(
        ownerId: Int,
        playlistId: Int,
        audioIds: List<String>
    ): Result<ApiResponse, DataError.Remote>
    public suspend fun createPlaylist(
        ownerId: Int,
        title: String,
        description: String? = null
    ): Result<ApiResponse, DataError.Remote>
    public suspend fun deletePlaylist(
        ownerId: Int,
        playlistId: Int
    ): Result<ApiResponse, DataError.Remote>
}
