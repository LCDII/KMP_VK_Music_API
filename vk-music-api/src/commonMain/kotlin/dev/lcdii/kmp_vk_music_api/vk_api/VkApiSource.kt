package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.core.ACCESS_TOKEN_KEY
import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.core.USER_AGENT_KEY
import dev.lcdii.kmp_vk_music_api.core.VkApiError
import dev.lcdii.kmp_vk_music_api.vk_api.data.VkApiJson
import dev.lcdii.kmp_vk_music_api.vk_api.data.VkMobileAppClient
import dev.lcdii.kmp_vk_music_api.vk_api.domain.AuthFlow
import dev.lcdii.kmp_vk_music_api.vk_api.domain.TokenStorage
import dev.lcdii.kmp_vk_music_api.vk_api.domain.PlaylistAttributes
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkApiRequest
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.VkApiResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.safeCall
import dev.lcdii.kmp_vk_music_api.vk_api.domain.ApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.domain.AppClient
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkCaptchaResolver
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkValidationResolver
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.forms.submitForm
import io.ktor.http.appendPathSegments
import io.ktor.http.parameters
import kotlinx.serialization.json.Json


public class VkApiSource(
    private val httpClient: HttpClient,
    private val appClient: AppClient = VkMobileAppClient,
    private val json: Json = VkApiJson,
    private val tokenStorage: TokenStorage
): ApiSource {
    public suspend fun isAuthorized(): Boolean {
        if (tokenStorage.getToken() == null) return false
        return getProfileInfo() is Result.Success
    }

    public suspend fun getUserId(): Result<Int, DataError.Remote> {
        if (tokenStorage.getToken() == null) {
            return Result.Error(DataError.Remote.NotAuthorized)
        }
        return when (val result = getProfileInfo()) {
            is Result.Success -> Result.Success(result.data.id)
            is Result.Error -> Result.Error(result.error)
        }
    }

    override suspend fun auth(
        login: String,
        password: String,
        captchaResolver: VkCaptchaResolver,
        validationResolver: VkValidationResolver
    ): Result<VkApiResponse.AuthResponse, DataError> {
        val flow = AuthFlow(
            httpClient = httpClient,
            appClient = appClient,
            json = json,
            login = login,
            password = password,
            captchaResolver = captchaResolver,
            validationResolver = validationResolver,
        )

        return when (val result = flow.run()) {
            is Result.Error -> Result.Error(result.error)

            is Result.Success -> {
                tokenStorage.saveToken(result.data.accessToken)
                Result.Success(
                    VkApiResponse.AuthResponse(userId = result.data.userId)
                )
            }
        }
    }

    override suspend fun getProfileInfo(): Result<VkApiResponse.ProfileInfo, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        return safeCall<VkApiResponse.ProfileInfo>(json){
            val request = VkApiRequest.GetProfileInfo(
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
            )
            httpClient.get {
                url.appendPathSegments(request.path)
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter(ACCESS_TOKEN_KEY, token)
            }
        }
    }

    override suspend fun getUsersAudioCount(userId: Int): Result<VkApiResponse.AudioGetCount, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        return safeCall<VkApiResponse.AudioGetCount>(json){
            val request = VkApiRequest.GetUsersAudioCount(
                ownerId = userId,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
            )

            httpClient.get {
                url.appendPathSegments(request.path)
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("owner_id", request.parameters.ownerId)
                parameter(ACCESS_TOKEN_KEY, token)
            }
        }
    }

    override suspend fun getUsersAudio(
        userId: Int,
        count: Int?,
        offset: Int?,
        playlistAttributes: PlaylistAttributes?
    ): Result<VkApiResponse.AudioGet, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        return safeCall<VkApiResponse.AudioGet>(json){
            val request = VkApiRequest.GetUsersAudio(
                userId,
                count,
                offset,
                playlistAttributes,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
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
                parameter(ACCESS_TOKEN_KEY, token)
            }
        }
    }

    override suspend fun getUsersPlaylists(
        userId: Int,
        count: Int?,
        offset: Int?
    ): Result<VkApiResponse.AudioGetPlayLists, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        return safeCall<VkApiResponse.AudioGetPlayLists>(json){
            val request = VkApiRequest.GetUsersPlaylists(
                userId,
                count,
                offset,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
            )

            httpClient.get {
                url.appendPathSegments(request.path)
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("owner_id", request.parameters.ownerId)
                parameter("count", request.parameters.count)
                parameter("offset", request.parameters.offset)
                parameter(ACCESS_TOKEN_KEY, token)
            }
        }
    }

    override suspend fun searchAudio(
        query: String,
        count: Int?,
        offset: Int?
    ): Result<VkApiResponse.AudioSearch, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        return safeCall<VkApiResponse.AudioSearch>(json){
            val request = VkApiRequest.SearchAudio(
                query,
                count,
                offset,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
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
                parameter(ACCESS_TOKEN_KEY, token)
            }
        }
    }

    public suspend fun getAudioById(
        audioIds: List<String>
    ): Result<VkApiResponse.AudioGetById, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        if (audioIds.isEmpty()) {
            return Result.Error(
                DataError.Remote.Api(
                    VkApiError.CodeError(
                        code = 100,
                        message = "audioIds must not be empty"
                    )
                )
            )
        }
        return safeCall<VkApiResponse.AudioGetById>(json) {
            val request = VkApiRequest.GetById(
                audioIds = audioIds,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
            )
            httpClient.get {
                url.appendPathSegments(request.path)
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("audios", request.parameters.audioIds.joinToString(","))
                parameter(ACCESS_TOKEN_KEY, token)
            }
        }
    }

    public suspend fun searchAlbums(
        query: String,
        count: Int? = null,
        offset: Int? = null
    ): Result<VkApiResponse.AudioSearchAlbums, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        return safeCall<VkApiResponse.AudioSearchAlbums>(json) {
            val request = VkApiRequest.SearchAlbums(
                query = query,
                count = count,
                offset = offset,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
            )
            httpClient.get {
                url.appendPathSegments(request.path)
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("q", request.parameters.query)
                parameter("count", request.parameters.count)
                parameter("offset", request.parameters.offset)
                parameter(ACCESS_TOKEN_KEY, token)
            }
        }
    }

    public suspend fun getPopular(
        count: Int? = null,
        offset: Int? = null
    ): Result<VkApiResponse.AudioGetPopular, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        return safeCall<VkApiResponse.AudioGetPopular>(json) {
            val request = VkApiRequest.GetPopular(
                count = count,
                offset = offset,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
            )
            httpClient.get {
                url.appendPathSegments(request.path)
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("count", request.parameters.count)
                parameter("offset", request.parameters.offset)
                parameter(ACCESS_TOKEN_KEY, token)
            }
        }
    }

    override suspend fun searchPlaylists(
        query: String,
        count: Int?,
        offset: Int?
    ): Result<VkApiResponse.AudioSearchPlaylists, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        return safeCall<VkApiResponse.AudioSearchPlaylists>(json){
            val request = VkApiRequest.SearchPlaylists(
                query,
                count,
                offset,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
            )

            httpClient.get {
                url.appendPathSegments(request.path)
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("q", request.parameters.query)
                parameter("count", request.parameters.count)
                parameter("offset", request.parameters.offset)
                parameter(ACCESS_TOKEN_KEY, token)
            }
        }
    }

    override suspend fun addAudio(
        ownerId: Int,
        audioId: Int
    ): Result<VkApiResponse.AddAudioResponse, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        return safeCall<VkApiResponse.AddAudioResponse>(json) {
            val request = VkApiRequest.AddAudio(
                ownerId = ownerId,
                audioId = audioId,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
            )
            httpClient.submitForm(
                url = request.path,
                formParameters = parameters {
                    append("owner_id", request.parameters.ownerId.toString())
                    append("audio_id", request.parameters.audioId.toString())
                    append(ACCESS_TOKEN_KEY, token)
                }
            ) {
                for ((key, value) in request.headers) {
                    header(key, value)
                }
            }
        }
    }

    override suspend fun addAudiosToPlaylist(
        ownerId: Int,
        playlistId: Int,
        audioIds: List<String>
    ): Result<VkApiResponse.AddAudiosToPlaylistResponse, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        if (audioIds.isEmpty()) {
            return Result.Error(
                DataError.Remote.Api(
                    VkApiError.CodeError(
                        code = 100,
                        message = "audioIds must not be empty"
                    )
                )
            )
        }
        return safeCall<VkApiResponse.AddAudiosToPlaylistResponse>(json) {
            val request = VkApiRequest.AddAudiosToPlaylist(
                ownerId = ownerId,
                playlistId = playlistId,
                audioIds = audioIds,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
            )
            httpClient.submitForm(
                url = request.path,
                formParameters = parameters {
                    append("owner_id", request.parameters.ownerId.toString())
                    append("playlist_id", request.parameters.playlistId.toString())
                    append("audio_ids", request.parameters.audioIds.joinToString(","))
                    append(ACCESS_TOKEN_KEY, token)
                }
            ) {
                for ((key, value) in request.headers) {
                    header(key, value)
                }
            }
        }
    }

    override suspend fun deleteAudio(
        ownerId: Int,
        audioId: Int
    ): Result<VkApiResponse.DeleteAudioResponse, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        return safeCall<VkApiResponse.DeleteAudioResponse>(json) {
            val request = VkApiRequest.DeleteAudio(
                ownerId = ownerId,
                audioId = audioId,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
            )
            httpClient.submitForm(
                url = request.path,
                formParameters = parameters {
                    append("owner_id", request.parameters.ownerId.toString())
                    append("audio_id", request.parameters.audioId.toString())
                    append(ACCESS_TOKEN_KEY, token)
                }
            ) {
                for ((key, value) in request.headers) {
                    header(key, value)
                }
            }
        }
    }

    override suspend fun removeAudiosFromPlaylist(
        ownerId: Int,
        playlistId: Int,
        audioIds: List<String>
    ): Result<VkApiResponse.RemoveAudiosFromPlaylistResponse, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        if (audioIds.isEmpty()) {
            return Result.Error(
                DataError.Remote.Api(
                    VkApiError.CodeError(
                        code = 100,
                        message = "audioIds must not be empty"
                    )
                )
            )
        }
        return safeCall<VkApiResponse.RemoveAudiosFromPlaylistResponse>(json) {
            val request = VkApiRequest.RemoveAudiosFromPlaylist(
                ownerId = ownerId,
                playlistId = playlistId,
                audioIds = audioIds,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
            )
            httpClient.submitForm(
                url = request.path,
                formParameters = parameters {
                    append("owner_id", request.parameters.ownerId.toString())
                    append("playlist_id", request.parameters.playlistId.toString())
                    append("audio_ids", request.parameters.audioIds.joinToString(","))
                    append(ACCESS_TOKEN_KEY, token)
                }
            ) {
                for ((key, value) in request.headers) {
                    header(key, value)
                }
            }
        }
    }

    override suspend fun createPlaylist(
        ownerId: Int,
        title: String,
        description: String?
    ): Result<VkApiResponse.CreatePlaylistResponse, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        return safeCall<VkApiResponse.CreatePlaylistResponse>(json) {
            val request = VkApiRequest.CreatePlaylist(
                ownerId = ownerId,
                title = title,
                description = description,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
            )
            httpClient.submitForm(
                url = request.path,
                formParameters = parameters {
                    append("owner_id", request.parameters.ownerId.toString())
                    append("title", request.parameters.title)
                    request.parameters.description?.let { append("description", it) }
                    append(ACCESS_TOKEN_KEY, token)
                }
            ) {
                for ((key, value) in request.headers) {
                    header(key, value)
                }
            }
        }
    }

    override suspend fun deletePlaylist(
        ownerId: Int,
        playlistId: Int
    ): Result<VkApiResponse.DeletePlaylistResponse, DataError.Remote> {
        val token = tokenStorage.getToken()
            ?: return Result.Error(DataError.Remote.NotAuthorized)
        return safeCall<VkApiResponse.DeletePlaylistResponse>(json) {
            val request = VkApiRequest.DeletePlaylist(
                ownerId = ownerId,
                playlistId = playlistId,
                headers = mapOf(USER_AGENT_KEY to appClient.userAgent)
            )
            httpClient.submitForm(
                url = request.path,
                formParameters = parameters {
                    append("owner_id", request.parameters.ownerId.toString())
                    append("playlist_id", request.parameters.playlistId.toString())
                    append(ACCESS_TOKEN_KEY, token)
                }
            ) {
                for ((key, value) in request.headers) {
                    header(key, value)
                }
            }
        }
    }
}
