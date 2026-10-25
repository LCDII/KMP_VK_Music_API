package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.vk_api.core.DataError
import dev.lcdii.kmp_vk_music_api.vk_api.core.Result
import dev.lcdii.kmp_vk_music_api.vk_api.data.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class AuthPayload(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("user_id")
    val userId: Int
)

class VkApiSource(
    private val httpClient: HttpClient,
    private val appClient: AppClient,
    private val json: Json,
    private val tokenStorage: TokenStorage
): ApiSource {
    override suspend fun getUserId(): Result<Int, DataError.Remote> {
        val token = tokenStorage.getToken()
        val userId = tokenStorage.getUserId()

        return if (token != null && userId != null) {
            Result.Success(userId)
        } else {
            Result.Error(DataError.Remote.NotAuthorized)
        }
    }

    override suspend fun auth(
        login: String,
        password: String,
        captchaResolver: VkCaptchaResolver,
        validationResolver: VkValidationResolver
    ): Result<ApiResponse, DataError.Remote> {
        val storedToken = tokenStorage.getToken()
        val storedUserId = tokenStorage.getUserId()
        if (storedToken != null && storedUserId != null) {
            return Result.Success(VkApiResponse.AuthResponse(userId = storedUserId))
        }

        var successToken: String? = null
        var validationCode: String? = null
        var validationError: VkApiError.TextError? = null

        while (true) {
            val request = VkApiRequest.Authenticate(
                login = login,
                password = password,
                appClient = appClient,
                successToken = successToken,
                code = validationCode,
                headers = mapOf("User-Agent" to appClient.userAgent)
            )

            when (val result = safeCall<AuthPayload>(json) {
                executeAuthenticate(request)
            }) {
                is Result.Success -> {
                    tokenStorage.saveToken(result.data.accessToken, result.data.userId)
                    return Result.Success(
                        VkApiResponse.AuthResponse(userId = result.data.userId)
                    )
                }

                is Result.Error -> {
                    val apiError = (result.error as? DataError.Remote.Api)?.error
                        ?: return Result.Error(result.error)

                    when (apiError) {
                        is VkApiError.TextError -> when (apiError.type) {
                            TextErrorType.NEED_CAPTCHA -> {
                                val redirectUri = apiError.redirectUri
                                    ?: return Result.Error(result.error)
                                successToken = captchaResolver.resolve(redirectUri)
                            }

                            TextErrorType.NEED_VALIDATION -> {
                                validationError = apiError
                                when (val validationResult = requestValidationCode(apiError)) {
                                    is Result.Error -> return Result.Error(validationResult.error)
                                    is Result.Success -> {
                                        validationCode = validationResolver.resolve(apiError)
                                    }
                                }
                            }

                            TextErrorType.INVALID_REQUEST -> {
                                val previousError = validationError
                                    ?: return Result.Error(result.error)
                                validationCode = validationResolver.resolve(
                                    previousError.copy(
                                        description = apiError.description
                                            ?: "Invalid validation code"
                                    )
                                )
                            }

                            else -> return Result.Error(result.error)
                        }

                        else -> return Result.Error(result.error)
                    }
                }
            }
        }
    }

    private suspend fun executeAuthenticate(
        request: VkApiRequest.Authenticate
    ) = httpClient.get(request.path) {
        for ((key, value) in request.headers) {
            header(key, value)
        }
        parameter("grant_type", "password")
        parameter("client_id", request.parameters.clientId)
        parameter("client_secret", request.parameters.clientSecret)
        parameter("username", request.parameters.login)
        parameter("password", request.parameters.password)
        parameter("scope", "audio,offline")
        parameter("2fa_supported", 1)
        parameter("force_sms", 1)
        parameter("v", "5.131")
        request.parameters.successToken?.let { parameter("success_token", it) }
        request.parameters.code?.let { parameter("code", it) }
    }

    private suspend fun requestValidationCode(
        error: VkApiError.TextError
    ): Result<VkApiResponse.ValidatePhoneResponse, DataError.Remote> {
        val sid = error.validationSid
            ?: return Result.Error(DataError.Remote.Api(error))
        val request = VkApiRequest.ValidatePhone(
            sid = sid,
            headers = mapOf("User-Agent" to appClient.userAgent)
        )

        return safeCall<VkApiResponse.ValidatePhoneResponse>(json) {
            httpClient.get(request.path) {
                for ((key, value) in request.headers) {
                    header(key, value)
                }
                parameter("sid", request.parameters.sid)
                parameter("v", "5.131")
            }
        }
    }

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
                tokenStorage.getToken()?.let { parameter("access_token", it) }
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
                tokenStorage.getToken()?.let { parameter("access_token", it) }
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
                tokenStorage.getToken()?.let { parameter("access_token", it) }
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
                tokenStorage.getToken()?.let { parameter("access_token", it) }
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
                tokenStorage.getToken()?.let { parameter("access_token", it) }
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
                tokenStorage.getToken()?.let { parameter("access_token", it) }
            }
        }
    }

}
