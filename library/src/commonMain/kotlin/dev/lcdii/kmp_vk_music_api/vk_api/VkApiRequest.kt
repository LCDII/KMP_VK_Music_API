package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.vk_api.components.ACCESS_TOKEN_KEY
import dev.lcdii.kmp_vk_music_api.vk_api.components.BASE_URL
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_EXTENDED
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_HTTPS
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_LANG
import dev.lcdii.kmp_vk_music_api.vk_api.components.DEFAULT_V
import dev.lcdii.kmp_vk_music_api.vk_api.components.USER_AGENT_KEY
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

enum class HttpMethod{
    GET,
    POST
}

data class Params(
    val https: Int = DEFAULT_HTTPS,
    val lang: String = DEFAULT_LANG,
    val extended: Int = DEFAULT_EXTENDED,
    val v: String = DEFAULT_V,
    val other: Map<String, JsonElement> = emptyMap()
) {

    fun addParams(params: Map<String, JsonElement>) : Params {
        var resultParams = this
        for (pair in params){
            if (pair.key !in listOf<String>("https", "lang", "extended", "v"))
            {
                resultParams = resultParams.addParam(pair.toPair())
            }
        }
        return resultParams
    }
    fun addParam(key: String, element: JsonElement) : Params {
        return this.copy(
            other = this.other + (key to element)
        )
    }
    fun addParam(pair: Pair<String, JsonElement>) : Params {
        return this.copy(
            other = this.other + pair
        )
    }
}

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
        urlContinuation: String
    ) : VkApiRequest {
        return this.copy(
            url = this.url + urlContinuation
        )
    }
}