package dev.lcdii.kmp_vk_music_api

import com.goncalossilva.resources.Resource
import dev.lcdii.kmp_vk_music_api.core.ACCESS_TOKEN_KEY
import dev.lcdii.kmp_vk_music_api.core.BASE_URL
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_EXTENDED
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_HTTPS
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_LANG
import dev.lcdii.kmp_vk_music_api.core.DEFAULT_V
import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.core.USER_AGENT_KEY
import dev.lcdii.kmp_vk_music_api.core.VkApiError
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.VkApiResponse
import dev.lcdii.kmp_vk_music_api.vk_api.domain.AppClient
import dev.lcdii.kmp_vk_music_api.vk_api.domain.TokenStorage
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkCaptchaResolver
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkValidationResolver
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

private const val LOGIN = "test_login"
private const val PASSWORD = "test_password"
private const val ACCESS_TOKEN = "access_token"
private const val USER_ID = 1234
private const val CAPTCHA_TOKEN = "captcha_success_token"
private const val CAPTCHA_REDIRECT_URI = "https://id.vk.com/captcha?sid=captcha_sid"
private const val VALIDATION_TYPE = "2fa_sms"
private const val VALIDATION_SID = "validation_sid"
private const val VALIDATION_CODE = "123456"
private const val NEW_VALIDATION_CODE = "654321"

class VkApiSourceAuthTest {

    private class TestTokenStorage(
        private var token: String? = null
    ) : TokenStorage {
        override suspend fun saveToken(token: String) {
            this.token = token
        }

        override suspend fun getToken(): String? = token

        override suspend fun clearToken() {
            token = null
        }

        override suspend fun replaceToken(token: String) {
            saveToken(token)
        }
    }

    private object TestAppClient : AppClient {
        override val userAgent = "test_user_agent"
        override val clientId = "test_client_id"
        override val clientSecret = "test_client_secret"
    }

    private object TestHttpClientFactory {
        fun create(engine: HttpClientEngine): HttpClient =
            HttpClient(engine) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            println(message)
                        }
                    }
                    level = LogLevel.ALL
                }
                defaultRequest {
                    url(BASE_URL)
                    url.parameters.append("https", DEFAULT_HTTPS.toString())
                    url.parameters.append("lang", DEFAULT_LANG)
                    url.parameters.append("extended", DEFAULT_EXTENDED.toString())
                    url.parameters.append("v", DEFAULT_V)
                }
            }
    }

    private val testJson = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private class TestCaptchaResolver(
        private val results: List<Result<String, DataError>> =
            listOf(Result.Success(CAPTCHA_TOKEN))
    ) : VkCaptchaResolver {
        var calls = 0
            private set

        var lastRedirectUri: String? = null
            private set

        override suspend fun resolve(redirectUri: String): Result<String, DataError> {
            lastRedirectUri = redirectUri
            return results[calls++]
        }
    }

    private class TestValidationResolver(
        private val results: List<Result<String, DataError>> =
            listOf(Result.Success(VALIDATION_CODE))
    ) : VkValidationResolver {
        var calls = 0
            private set

        val receivedErrors = mutableListOf<VkApiError.Auth.Validation>()

        override suspend fun resolve(
            error: VkApiError.Auth.Validation
        ): Result<String, DataError> {
            receivedErrors += error
            return results[calls++]
        }
    }

    private fun assertAuthRequestParameters(
        request: HttpRequestData,
        expectedSuccessToken: String? = null,
        expectedCode: String? = null
    ) {
        assertEquals(HttpMethod.Get, request.method)
        assertEquals("oauth.vk.com", request.url.host)
        assertEquals("/token", request.url.encodedPath)
        assertEquals(TestAppClient.userAgent, request.headers[USER_AGENT_KEY])
        assertEquals("password", request.url.parameters["grant_type"])
        assertEquals(TestAppClient.clientId, request.url.parameters["client_id"])
        assertEquals(TestAppClient.clientSecret, request.url.parameters["client_secret"])
        assertEquals(LOGIN, request.url.parameters["username"])
        assertEquals(PASSWORD, request.url.parameters["password"])
        assertEquals("audio,offline", request.url.parameters["scope"])
        assertEquals("1", request.url.parameters["2fa_supported"])
        assertEquals("1", request.url.parameters["force_sms"])
        assertEquals(DEFAULT_V, request.url.parameters["v"])
        assertEquals(expectedSuccessToken, request.url.parameters["success_token"])
        assertEquals(expectedCode, request.url.parameters["code"])
    }

    private fun assertValidatePhoneRequest(request: HttpRequestData) {
        assertEquals(HttpMethod.Get, request.method)
        assertEquals(Url(BASE_URL).host, request.url.host)
        assertEquals("/method/auth.validatePhone", request.url.encodedPath)
        assertEquals(VALIDATION_SID, request.url.parameters["sid"])
        assertEquals(DEFAULT_V, request.url.parameters["v"])
        assertEquals(TestAppClient.userAgent, request.headers[USER_AGENT_KEY])
        assertNull(request.url.parameters[ACCESS_TOKEN_KEY])
    }

    @Test
    fun `auth returns success immediately`() = runTest {
        val tokenStorage = TestTokenStorage()
        var tokenRequests = 0

        val engine = MockEngine { request ->
            tokenRequests++
            assertAuthRequestParameters(request)
            respond(
                content = Resource("json_responses/auth/success.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val captchaResolver = TestCaptchaResolver()
        val validationResolver = TestValidationResolver()
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = tokenStorage
        )

        val result = api.auth(LOGIN, PASSWORD, captchaResolver, validationResolver)

        val success = assertIs<Result.Success<VkApiResponse.AuthResponse>>(result)
        assertEquals(USER_ID, success.data.userId)
        assertEquals(ACCESS_TOKEN, tokenStorage.getToken())
        assertEquals(1, tokenRequests)
        assertEquals(0, captchaResolver.calls)
        assertEquals(0, validationResolver.calls)
    }

    @Test
    fun `auth handles need captcha`() = runTest {
        val tokenStorage = TestTokenStorage()
        var tokenRequests = 0

        val engine = MockEngine { request ->
            tokenRequests++

            when (tokenRequests) {
                1 -> {
                    assertAuthRequestParameters(request)
                    respond(
                        content = Resource("json_responses/auth/need_captcha.json").readText(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }

                else -> {
                    assertAuthRequestParameters(
                        request,
                        expectedSuccessToken = CAPTCHA_TOKEN
                    )
                    respond(
                        content = Resource("json_responses/auth/success.json").readText(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
        }

        val captchaResolver = TestCaptchaResolver()
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = tokenStorage
        )

        val result = api.auth(
            login = LOGIN,
            password = PASSWORD,
            captchaResolver = captchaResolver,
            validationResolver = TestValidationResolver()
        )

        assertIs<Result.Success<VkApiResponse.AuthResponse>>(result)
        assertEquals(1, captchaResolver.calls)
        assertEquals(CAPTCHA_REDIRECT_URI, captchaResolver.lastRedirectUri)
        assertEquals(ACCESS_TOKEN, tokenStorage.getToken())
    }

    @Test
    fun `auth handles need validation`() = runTest {
        val tokenStorage = TestTokenStorage()

        val engine = MockEngine { request ->
            when {
                request.url.host == "oauth.vk.com" -> {
                    val code = request.url.parameters["code"]

                    if (code == null) {
                        assertAuthRequestParameters(request)
                        respond(
                            content = Resource("json_responses/auth/need_validation.json").readText(),
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    } else {
                        assertAuthRequestParameters(
                            request,
                            expectedCode = VALIDATION_CODE
                        )
                        respond(
                            content = Resource("json_responses/auth/success.json").readText(),
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    }
                }

                request.url.encodedPath == "/method/auth.validatePhone" -> {
                    assertValidatePhoneRequest(request)
                    respond(
                        content = Resource("json_responses/auth/validate_phone.json").readText(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }

                else -> error("Unexpected request: ${request.url}")
            }
        }

        val validationResolver = TestValidationResolver()
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = tokenStorage
        )

        val result = api.auth(
            login = LOGIN,
            password = PASSWORD,
            captchaResolver = TestCaptchaResolver(),
            validationResolver = validationResolver
        )

        assertIs<Result.Success<VkApiResponse.AuthResponse>>(result)
        assertEquals(1, validationResolver.calls)

        val validation = validationResolver.receivedErrors.single()
        assertEquals(VALIDATION_TYPE, validation.validationType)
        assertEquals(VALIDATION_SID, validation.sid)
        assertEquals("Enter the code from SMS", validation.description)
        assertEquals(ACCESS_TOKEN, tokenStorage.getToken())
    }

    @Test
    fun `auth returns error on invalid credentials`() = runTest {
        val tokenStorage = TestTokenStorage()
        var tokenRequests = 0

        val engine = MockEngine { request ->
            tokenRequests++
            assertAuthRequestParameters(request)
            respond(
                content = Resource("json_responses/auth/invalid_client.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val captchaResolver = TestCaptchaResolver()
        val validationResolver = TestValidationResolver()
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = tokenStorage
        )

        val result = api.auth(LOGIN, PASSWORD, captchaResolver, validationResolver)

        val error = assertIs<Result.Error<DataError.Remote.Api>>(result)
        assertIs<VkApiError.Auth.InvalidClient>(error.error.error)
        assertNull(tokenStorage.getToken())
        assertEquals(1, tokenRequests)
        assertEquals(0, captchaResolver.calls)
        assertEquals(0, validationResolver.calls)
    }

    @Test
    fun `auth asks for new code when validation code is invalid`() = runTest {
        val tokenStorage = TestTokenStorage()

        val engine = MockEngine { request ->
            when {
                request.url.host == "oauth.vk.com" -> {
                    when (request.url.parameters["code"]) {
                        null -> {
                            assertAuthRequestParameters(request)
                            respond(
                                content = Resource("json_responses/auth/need_validation.json").readText(),
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        }

                        VALIDATION_CODE -> {
                            assertAuthRequestParameters(
                                request,
                                expectedCode = VALIDATION_CODE
                            )
                            respond(
                                content = Resource("json_responses/auth/invalid_validation_code.json").readText(),
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        }

                        NEW_VALIDATION_CODE -> {
                            assertAuthRequestParameters(
                                request,
                                expectedCode = NEW_VALIDATION_CODE
                            )
                            respond(
                                content = Resource("json_responses/auth/success.json").readText(),
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        }

                        else -> error("Unexpected code: ${request.url.parameters["code"]}")
                    }
                }

                request.url.encodedPath == "/method/auth.validatePhone" -> {
                    assertValidatePhoneRequest(request)
                    respond(
                        content = Resource("json_responses/auth/validate_phone.json").readText(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }

                else -> error("Unexpected request: ${request.url}")
            }
        }

        val validationResolver = TestValidationResolver(
            results = listOf(
                Result.Success(VALIDATION_CODE),
                Result.Success(NEW_VALIDATION_CODE)
            )
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = tokenStorage
        )

        val result = api.auth(
            login = LOGIN,
            password = PASSWORD,
            captchaResolver = TestCaptchaResolver(),
            validationResolver = validationResolver
        )

        assertIs<Result.Success<VkApiResponse.AuthResponse>>(result)
        assertEquals(2, validationResolver.calls)
        assertEquals(
            "Enter the code from SMS",
            validationResolver.receivedErrors[0].description
        )
        assertEquals(
            "Code is invalid",
            validationResolver.receivedErrors[1].description
        )
        assertEquals(ACCESS_TOKEN, tokenStorage.getToken())
    }

    @Test
    fun `auth returns error when captcha resolver fails`() = runTest {
        val tokenStorage = TestTokenStorage()
        var tokenRequests = 0

        val engine = MockEngine { request ->
            tokenRequests++
            assertAuthRequestParameters(request)
            respond(
                content = Resource("json_responses/auth/need_captcha.json").readText(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val captchaResolver = TestCaptchaResolver(
            results = listOf(
                Result.Error(DataError.Remote.CaptchaFailed("captcha failed"))
            )
        )
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = tokenStorage
        )

        val result = api.auth(
            login = LOGIN,
            password = PASSWORD,
            captchaResolver = captchaResolver,
            validationResolver = TestValidationResolver()
        )

        val error = assertIs<Result.Error<DataError.Remote.CaptchaFailed>>(result)
        assertEquals("captcha failed", error.error.msg)
        assertEquals(1, tokenRequests)
        assertNull(tokenStorage.getToken())
    }

    @Test
    fun `auth handles captcha and validation in one session`() = runTest {
        val tokenStorage = TestTokenStorage()

        val engine = MockEngine { request ->
            when {
                request.url.host == "oauth.vk.com" -> {
                    val successToken = request.url.parameters["success_token"]
                    val code = request.url.parameters["code"]

                    when {
                        successToken == null && code == null -> {
                            assertAuthRequestParameters(request)
                            respond(
                                content = Resource("json_responses/auth/need_captcha.json").readText(),
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        }

                        successToken != null && code == null -> {
                            assertAuthRequestParameters(
                                request,
                                expectedSuccessToken = CAPTCHA_TOKEN
                            )
                            respond(
                                content = Resource("json_responses/auth/need_validation.json").readText(),
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        }

                        successToken != null && code != null -> {
                            assertAuthRequestParameters(
                                request,
                                expectedSuccessToken = CAPTCHA_TOKEN,
                                expectedCode = VALIDATION_CODE
                            )
                            respond(
                                content = Resource("json_responses/auth/success.json").readText(),
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        }

                        else -> error("Unexpected auth request: ${request.url}")
                    }
                }

                request.url.encodedPath == "/method/auth.validatePhone" -> {
                    assertValidatePhoneRequest(request)
                    respond(
                        content = Resource("json_responses/auth/validate_phone.json").readText(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }

                else -> error("Unexpected request: ${request.url}")
            }
        }

        val captchaResolver = TestCaptchaResolver()
        val validationResolver = TestValidationResolver()
        val api = VkApiSource(
            httpClient = TestHttpClientFactory.create(engine),
            appClient = TestAppClient,
            json = testJson,
            tokenStorage = tokenStorage
        )

        val result = api.auth(LOGIN, PASSWORD, captchaResolver, validationResolver)

        assertIs<Result.Success<VkApiResponse.AuthResponse>>(result)
        assertEquals(1, captchaResolver.calls)
        assertEquals(1, validationResolver.calls)
        assertEquals(ACCESS_TOKEN, tokenStorage.getToken())
    }
}
