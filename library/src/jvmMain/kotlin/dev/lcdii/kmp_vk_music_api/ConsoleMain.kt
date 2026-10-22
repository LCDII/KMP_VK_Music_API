package dev.lcdii.kmp_vk_music_api

import dev.lcdii.kmp_vk_music_api.di.vkApiModule
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.core.onError
import dev.lcdii.kmp_vk_music_api.vk_api.core.onSuccess
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin

fun main(args: Array<String>): Unit = runBlocking {
    val userId = 336591731

    val koin = startKoin {
        modules(vkApiModule)
    }.koin

    val apiSource = VkApiSource(
        httpClient = koin.get(),
        appClient = koin.get(),
        json = koin.get<Json>()
    )

    apiSource.getUsersAudioCount(userId)
        .onSuccess { response ->
            println("Audio count: ${response.count}")
        }
        .onError { error ->
            println("Audio count request failed: $error")
        }

    apiSource.getProfileInfo()
        .onSuccess { response ->
            println("Name: ${response.screenName}")
        }
        .onError { error ->
            println("Profile request failed: $error")
        }

    apiSource.getUsersAudio(userId, 3)
        .onSuccess { response ->
            for (song in response.items){
                println(song.title)
            }
        }
        .onError { error ->
            println("Audio get request failed: $error")
        }

    apiSource.getUsersPlaylist(userId, 3)
        .onSuccess { response ->
            for (playlist in response.items){
                println(playlist.title)
            }
        }
        .onError { error ->
            println("Playlists get request failed: $error")
        }

    val query: String = "Take Me To The Sun"
    apiSource.searchAudio(query, 3)
        .onSuccess { response ->
            for (song in response.items){
                println(song.title)
            }
        }
        .onError { error ->
            println("Search Audio request failed: $error")
        }

    apiSource.searchPlaylists(query, 3)
        .onSuccess { response ->
            for (playlist in response.items){
                println(playlist.title)
            }
        }
        .onError { error ->
            println("Search Playlist request failed: $error")
        }
}
