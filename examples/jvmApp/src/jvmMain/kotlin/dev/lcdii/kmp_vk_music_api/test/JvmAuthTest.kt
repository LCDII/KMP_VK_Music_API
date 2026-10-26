package dev.lcdii.kmp_vk_music_api.test

import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.di.VkApiKoinModules
import dev.lcdii.kmp_vk_music_api.vk_api.JvmAuth
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.VkApiResponse
import dev.lcdii.kmp_vk_music_api.vk_api.domain.ApiSource
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun main(): Unit = runBlocking {
    val koin = startKoin {
        modules(VkApiKoinModules.modules)
    }.koin

    try {
        try {
            when (val userIdResult = koin.get<ApiSource>().getUserId()) {
                is Result.Success -> {
                    println("Already authorized")
                    println("user_id: ${userIdResult.data}")
                    return@runBlocking
                }

                is Result.Error -> Unit
            }
        } catch (_: Throwable) {

        }

        print("VK login: ")
        val login = readln()

        print("VK password: ")
        val password = readln()

        when (val result = koin.get<JvmAuth>().auth(login, password)) {
            is Result.Success -> {
                val authResponse = result.data as? VkApiResponse.AuthResponse
                println("Authorization successful")
                println("user_id: ${authResponse?.userId ?: "not returned"}")
            }

            is Result.Error -> {
                println("Authorization failed")
                println("Error: ${result.error}")
            }
        }
    } finally {
        stopKoin()
    }
}
