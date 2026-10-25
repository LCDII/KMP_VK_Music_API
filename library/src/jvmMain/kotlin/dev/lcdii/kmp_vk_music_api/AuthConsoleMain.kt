package dev.lcdii.kmp_vk_music_api

import dev.lcdii.kmp_vk_music_api.di.vkApiModule
import dev.lcdii.kmp_vk_music_api.vk_api.JvmAuth
import dev.lcdii.kmp_vk_music_api.vk_api.ApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiResponse
import dev.lcdii.kmp_vk_music_api.vk_api.core.Result
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun main() = runBlocking {
    startKoin {
        modules(vkApiModule)
    }

    try {
        try {
            when (val userIdResult = org.koin.core.context.GlobalContext.get().get<ApiSource>().getUserId()) {
                is Result.Success -> {
                    println("Already authorized")
                    println("user_id: ${userIdResult.data}")
                    return@runBlocking
                }

                is Result.Error -> Unit
            }
        } catch (_: Throwable) {
            // Continue with interactive authorization when storage is unavailable.
        }

        print("VK login: ")
        val login = readln()

        print("VK password: ")
        val password = readln()

        when (val result = JvmAuth.auth(login, password)) {
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
