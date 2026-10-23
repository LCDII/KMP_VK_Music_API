package dev.lcdii.kmp_vk_music_api

import dev.lcdii.kmp_vk_music_api.di.vkApiModule
import dev.lcdii.kmp_vk_music_api.vk_api.JvmAuth
import dev.lcdii.kmp_vk_music_api.vk_api.TokenStorage
import dev.lcdii.kmp_vk_music_api.vk_api.core.Result
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun main() = runBlocking {
    print("VK login: ")
    val login = readln()

    print("VK password: ")
    val password = readln()

    startKoin {
        modules(vkApiModule)
    }

    try {
        when (val result = JvmAuth.authenticate(login, password)) {
            is Result.Success -> {
                println("Authorization successful")
                println("Token: ${GlobalContext.get().get<TokenStorage>().getToken() ?: "not saved"}")
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
