package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.vk_api.core.DataError
import dev.lcdii.kmp_vk_music_api.vk_api.core.Result
import dev.lcdii.kmp_vk_music_api.vk_api.core.TOKEN
import dev.lcdii.kmp_vk_music_api.vk_api.data.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import kotlinx.serialization.json.Json

class VkApiSource(
    private val httpClient: HttpClient,
    private val appClient: AppClient,
    private val json: Json
): ApiSource {
    override suspend fun getProfileInfo(): Result<ApiResponse, DataError.Remote> {
        TODO()
    }

    override suspend fun getUsersAudioCount(userId: Int): Result<ApiResponse, DataError.Remote> {
        return safeCall<VkApiResponse.AudioGetCount>(json){
            val request = VkApiRequest.GetUsersAudioCount(
                ownerId = userId,
                headers = mapOf("User-Agent" to appClient.userAgent)
            )

            httpClient.get {
                url {
                    appendPathSegments(request.path)
                }
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("owner_id", request.parameters.ownerId)
                parameter("access_token", TOKEN)
            }
        }
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
