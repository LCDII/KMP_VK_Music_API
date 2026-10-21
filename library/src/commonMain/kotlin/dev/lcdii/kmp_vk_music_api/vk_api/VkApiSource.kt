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
    override suspend fun getProfileInfo(): Result<VkApiResponse.ProfileInfo, DataError.Remote> {
        return safeCall<VkApiResponse.ProfileInfo>(json){
            val request = VkApiRequest.GetProfileInfo(
                headers = mapOf("User-Agent" to appClient.userAgent)
            )
            httpClient.get {
                url.appendPathSegments(request.path)
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("access_token", TOKEN)
            }
        }
    }

    override suspend fun getUsersAudioCount(userId: Int): Result<VkApiResponse.AudioGetCount, DataError.Remote> {
        return safeCall<VkApiResponse.AudioGetCount>(json){
            val request = VkApiRequest.GetUsersAudioCount(
                ownerId = userId,
                headers = mapOf("User-Agent" to appClient.userAgent)
            )

            httpClient.get {
                url.appendPathSegments(request.path)
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
    ): Result<VkApiResponse.AudioGet, DataError.Remote> {
        return safeCall<VkApiResponse.AudioGet>(json){
            val request = VkApiRequest.GetUsersAudio(
                userId,
                count,
                offset,
                playlistAttributes,
                headers = mapOf("User-Agent" to appClient.userAgent)
            )

            httpClient.get {
                url.appendPathSegments(request.path)
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("owner_id", request.parameters.ownerId)
                parameter("count", request.parameters.count)
                parameter("offset", request.parameters.offset)
                parameter("album_id", request.parameters.playlistAttributes?.playlistId)
                parameter("access_key", request.parameters.playlistAttributes?.accessKey)
                parameter("access_token", TOKEN)
            }
        }
    }

    override suspend fun getUsersPlaylist(
        userId: Int,
        count: Int?,
        offset: Int?
    ): Result<VkApiResponse.AudioGetPlayLists, DataError.Remote> {
        return safeCall<VkApiResponse.AudioGetPlayLists>(json){
            val request = VkApiRequest.GetUsersPlaylists(
                userId,
                count,
                offset,
                headers = mapOf("User-Agent" to appClient.userAgent)
            )

            httpClient.get {
                url.appendPathSegments(request.path)
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("owner_id", request.parameters.ownerId)
                parameter("count", request.parameters.count)
                parameter("offset", request.parameters.offset)
                parameter("access_token", TOKEN)
            }
        }
    }

    override suspend fun searchAudio(
        query: String,
        count: Int?,
        offset: Int?
    ): Result<VkApiResponse.AudioSearch, DataError.Remote> {
        return safeCall<VkApiResponse.AudioSearch>(json){
            val request = VkApiRequest.SearchAudio(
                query,
                count,
                offset,
                headers = mapOf("User-Agent" to appClient.userAgent)
            )

            httpClient.get {
                url.appendPathSegments(request.path)
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("q", request.parameters.query)
                parameter("count", request.parameters.count)
                parameter("offset", request.parameters.offset)
                parameter("sort", request.parameters.sort)
                parameter("autocomplete", request.parameters.autocomplete)
                parameter("access_token", TOKEN)
            }
        }
    }

    override suspend fun searchPlaylists(
        query: String,
        count: Int?,
        offset: Int?
    ): Result<VkApiResponse.AudioSearchPlaylists, DataError.Remote> {
        return safeCall<VkApiResponse.AudioSearchPlaylists>(json){
            val request = VkApiRequest.SearchPlaylists(
                query,
                count,
                offset,
                headers = mapOf("User-Agent" to appClient.userAgent)
            )

            httpClient.get {
                url.appendPathSegments(request.path)
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("q", request.parameters.query)
                parameter("count", request.parameters.count)
                parameter("offset", request.parameters.offset)
                parameter("access_token", TOKEN)
            }
        }
    }

}
