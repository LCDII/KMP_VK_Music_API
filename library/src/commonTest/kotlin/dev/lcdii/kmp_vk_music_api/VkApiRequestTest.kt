package dev.lcdii.kmp_vk_music_api

import dev.lcdii.kmp_vk_music_api.vk_api.Params
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiRequest
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class VkApiRequestTest {
    @Test
    fun paramsCreationWithoutOtherIsValid() {
        val params = Params()
        val result = "Params(https=1, lang=ru, extended=1, v=5.131, other={})"
        assertEquals(params.toString(), result)
    }
    @Test
    fun paramsCreationWithOtherIsValid() {
        val other = mapOf(("owner_id" to JsonPrimitive(123)))
        val params = Params( other = other)
        val result = "Params(https=1, lang=ru, extended=1, v=5.131, other={owner_id=123})"
        assertEquals(params.toString(), result)
    }
    @Test
    fun singleAdditionalParamsIsValid() {
        val params = Params().addParam("owner_id", JsonPrimitive(123))
        val result = "Params(https=1, lang=ru, extended=1, v=5.131, other={owner_id=123})"
        assertEquals(params.toString(), result)
    }
    @Test
    fun multipleAdditionalParamsAreValid() {
        val other = mapOf(("owner_id" to JsonPrimitive(123)), ("count" to JsonPrimitive(1)))
        val params = Params().addParams(other)
        val result = "Params(https=1, lang=ru, extended=1, v=5.131, other={owner_id=123, count=1})"
        assertEquals(params.toString(), result)
    }
    @Test
    fun existedParamsFieldAreIgnoredWhenOtherAdded() {
        val other = mapOf(("owner_id" to JsonPrimitive(123)), ("count" to JsonPrimitive(1)),
            ("https" to JsonPrimitive(3)))
        val params = Params().addParams(other)
        val result = "Params(https=1, lang=ru, extended=1, v=5.131, other={owner_id=123, count=1})"
        assertEquals(params.toString(), result)
    }
    @Test
    fun vkApiRequestCreationIsValid() {
        val request = VkApiRequest()
        val result = "VkApiRequest(method=GET, url=https://api.vk.com/method/, headers={}, params=" +
                "Params(https=1, lang=ru, extended=1, v=5.131, other={}))"
        assertEquals(request.toString(), result)
    }
    @Test
    fun vkApiRequestTokenIsAddable() {
        val request = VkApiRequest().addToken("token")
        val result = "VkApiRequest(method=GET, url=https://api.vk.com/method/, headers={}, params=" +
                "Params(https=1, lang=ru, extended=1, v=5.131, other={access_token=\"token\"}))"
        assertEquals(request.toString(), result)
    }
    @Test
    fun vkApiRequestUserAgentIsAddable() {
        val request = VkApiRequest().addUserAgent("agent")
        val result = "VkApiRequest(method=GET, url=https://api.vk.com/method/, " +
                "headers={User-Agent=agent}, params=Params(https=1, lang=ru, extended=1, v=5.131, other={}))"
        assertEquals(request.toString(), result)
    }
    @Test
    fun vkApiRequestUrlCanBeModified() {
        val request = VkApiRequest().modifyUrl("song")
        val result = "VkApiRequest(method=GET, url=https://api.vk.com/method/song, headers={}, params=" +
                "Params(https=1, lang=ru, extended=1, v=5.131, other={}))"
        assertEquals(request.toString(), result)
    }
}