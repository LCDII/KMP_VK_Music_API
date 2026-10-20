package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.vk_api.core.DataError
import dev.lcdii.kmp_vk_music_api.vk_api.core.Result
import io.ktor.client.HttpClient

class VkApiSource(
    private val httpClient: HttpClient,
    private val appClient: AppClient
): ApiSource {
    override suspend fun getProfileInfo(): Result<ApiResponse, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersAudioCount(userId: Int): Result<ApiResponse, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersAudio(
        userId: Int,
        count: Int?,
        offset: Int?,
        playlistAttributes: PlaylistAttributes?
    ): Result<ApiResponse, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersPlaylist(
        userId: Int,
        count: Int?,
        offset: Int?
    ): Result<ApiResponse, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun searchAudio(
        query: String,
        count: Int?,
        offset: Int?
    ): Result<ApiResponse, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun searchPlaylists(
        query: String,
        count: Int?,
        offset: Int?
    ): Result<ApiResponse, DataError.Remote> {
        TODO("Not yet implemented")
    }

}