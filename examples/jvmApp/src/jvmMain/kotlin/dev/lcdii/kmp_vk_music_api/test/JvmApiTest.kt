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
                    println(song.url)
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

        val audioOwnerId = userId
        val audioId = 456671411
        val ma = 456241000
        val mo = 336591731

        val playlistAudioIds = listOf<String>(
            "${174278282}_${456240589}"
        )

        apiSource.addAudio(174278282, 456240589)
            .onSuccess { response ->
                println("Audio added: ${response}")
            }
            .onError { error ->
                println("Add audio request failed: $error")
            }



        apiSource.addAudiosToPlaylist(
            ownerId = 336591731,
            playlistId = 10,
            audioIds = playlistAudioIds
        )
            .onSuccess { response ->
                println("Audios added to playlist: ${response}")
            }
            .onError { error ->
                println("Add audios to playlist request failed: $error")
            }
        /*
        apiSource.removeAudiosFromPlaylist(
            ownerId = playlistOwnerId,
            playlistId = playlistId,
            audioIds = playlistAudioIds
        )
            .onSuccess { response ->
                println("Audios removed from playlist: ${response}")
            }
            .onError { error ->
                println("Remove audios from playlist request failed: $error")
            }
        */
        apiSource.createPlaylist(
            ownerId = 336591731,
            title = "test",
            description = "test"
        )
            .onSuccess { response ->
                println("Playlist created: ${response}")
            }
            .onError { error ->
                println("Create playlist request failed: $error")
            }

//        apiSource.deletePlaylist(336591731, 12)
//            .onSuccess { response ->
//                println("Playlist deleted: ${response}")
//            }
//            .onError { error ->
//                println("Delete playlist request failed: $error")
//            }

    } finally {
        stopKoin()
    }
}
