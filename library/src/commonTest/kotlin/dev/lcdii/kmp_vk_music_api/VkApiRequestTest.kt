package dev.lcdii.kmp_vk_music_api

import dev.lcdii.kmp_vk_music_api.vk_api.Params
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class VkApiRequestTest {
    @Test
    fun paramsCreationWithoutOtherIsAvailable(){
        val params = Params()
        val result = "Params(https=1, lang=ru, extended=1, v=5.131, other={})"
        assertEquals(params.toString(), result)
    }
    @Test
    fun paramsCreationWithOtherIsAvailable(){
        val other = mapOf(("owner_id" to JsonPrimitive(123)))
        val params = Params( other = other)
        val result = "Params(https=1, lang=ru, extended=1, v=5.131, other={owner_id=123})"
        assertEquals(params.toString(), result)
    }
    @Test
    fun singleAdditionalParamsIsAddable(){
        val params = Params().addParam("owner_id", JsonPrimitive(123))
        val result = "Params(https=1, lang=ru, extended=1, v=5.131, other={owner_id=123})"
        assertEquals(params.toString(), result)
    }
    @Test
    fun multipleAdditionalParamsAreAddable(){
        val other = mapOf(("owner_id" to JsonPrimitive(123)), ("count" to JsonPrimitive(1)))
        val params = Params( other = other)
        val result = "Params(https=1, lang=ru, extended=1, v=5.131, other={owner_id=123, count=1})"
        assertEquals(params.toString(), result)
    }
}