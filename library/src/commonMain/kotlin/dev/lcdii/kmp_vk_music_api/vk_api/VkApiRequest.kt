package dev.lcdii.kmp_vk_music_api.vk_api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

private const val BASE_URL = "https://api.vk.com/method/"
private const val DEFAULT_LANG = "ru"
private const val DEFAULT_V = "5.131"
private const val DEFAULT_HTTPS = 1
private const val DEFAULT_EXTENDED = 1
private const val ACCESS_TOKEN_KEY = "access_token"
private const val USER_AGENT_KEY = "User-Agent"
@Serializable
enum class HttpMethod{
    @SerialName("get")
    GET,
    @SerialName("post")
    POST
}

@Serializable
data class Params(
    val https: Int = DEFAULT_HTTPS,
    val lang: String = DEFAULT_LANG,
    val extended: Int = DEFAULT_EXTENDED,
    val v: String = DEFAULT_V,
    val other: Map<String, JsonElement> = emptyMap()
) {

    fun addParams(params: Map<String, JsonElement>) : Params {
        return this.copy(
            other = this.other + params
        )
    }
    fun addParam(key: String, element: JsonElement) : Params {
        return this.copy(
            other = this.other + (key to element)
        )
    }
}

@Serializable
data class VkApiRequest(
    val method: HttpMethod = HttpMethod.GET,
    val url: String = BASE_URL,
    val headers: Map<String, String> =  emptyMap(),
    val params: Params = Params()
) {
    fun addToken(token: String): VkApiRequest {
        return this.copy(
            params = this.params.addParam(ACCESS_TOKEN_KEY, JsonPrimitive(token))
            )
    }
    fun addUserAgent(userAgent: String): VkApiRequest{
        return this.copy(
            headers = headers + (USER_AGENT_KEY to userAgent)
        )
    }

    fun modifyUrl(
        urlContinuation: String? = null
    ) : VkApiRequest {
        return this.copy(
            url = if (urlContinuation != null) this.url + urlContinuation
                else this.url
        )
    }
}