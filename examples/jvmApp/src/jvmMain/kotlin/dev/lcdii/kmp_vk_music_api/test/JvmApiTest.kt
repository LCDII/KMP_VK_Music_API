package dev.lcdii.kmp_vk_music_api.test

import dev.lcdii.kmp_vk_music_api.core.onError
import dev.lcdii.kmp_vk_music_api.core.onSuccess
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.di.VkApiKoinModules
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiSource
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun main(): Unit = runBlocking {
    val koin = startKoin {
        modules(VkApiKoinModules.modules)
    }.koin

    try {
        val apiSource = koin.get<VkApiSource>()

        val userId = when (val result = apiSource.getUserId()) {
            is Result.Success -> result.data
            is Result.Error -> {
                println("Unable to get user id: ${result.error}")
                return@runBlocking
            }
        }

        apiSource.getUsersAudioCount(userId)
            .onSuccess { response ->
                println("Audio count: ${response.count}")
            }
            .onError { error ->
                println("Audio count request failed: $error")
            }

        apiSource.getProfileInfo()
            .onSuccess { response ->
                println("Name: ${response.screenName ?: "not specified"}")
            }
            .onError { error ->
                println("Profile request failed: $error")
            }

        apiSource.getUsersAudio(userId, 3)
            .onSuccess { response ->
                response.items.forEach { song ->
                    println(song.title)
                }
            }
            .onError { error ->
                println("Audio get request failed: $error")
            }

        apiSource.getUsersPlaylist(userId, 3)
            .onSuccess { response ->
                response.items.forEach { playlist ->
                    println(playlist.title)
                }
            }
            .onError { error ->
                println("Playlists get request failed: $error")
            }

        val query = "Take Me To The Sun"
        apiSource.searchAudio(query, 3)
            .onSuccess { response ->
                response.items.forEach { song ->
                    println(song.title)
                }
            }
            .onError { error ->
                println("Search Audio request failed: $error")
            }

        apiSource.searchPlaylists(query, 3)
            .onSuccess { response ->
                response.items.forEach { playlist ->
                    println(playlist.title)
                }
            }
            .onError { error ->
                println("Search Playlist request failed: $error")
            }
    } finally {
        stopKoin()
    }
}
