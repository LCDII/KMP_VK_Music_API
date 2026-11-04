package dev.lcdii.kmp_vk_music_api.test

import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.core.VkApiError
import dev.lcdii.kmp_vk_music_api.di.VkApiKoinModules
import dev.lcdii.kmp_vk_music_api.vk_api.JvmAuth
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.AlbumResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.SongResponse
import dev.lcdii.kmp_vk_music_api.vk_api.domain.PlaylistAttributes
import dev.lcdii.kmp_vk_music_api.vk_api.domain.TokenStorage
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun main(): Unit = runBlocking {
    val koin = startKoin {
        modules(VkApiKoinModules.modules)
    }.koin

    try {
        val api = koin.get<VkApiSource>()
        val auth = koin.get<JvmAuth>()
        val tokenStorage = koin.get<TokenStorage>()

        var currentUserId: Int

        if (!api.isAuthorized()) {
            println("Нужна авторизация")

            print("VK login: ")
            val login = readln()

            print("VK password: ")
            val password = readln()

            when (val authResult = auth.auth(login, password)) {
                is Result.Error -> {
                    println("Ошибка авторизации: ${describeError(authResult.error)}")
                    return@runBlocking
                }
                is Result.Success -> Unit
            }

            currentUserId = when (val result = api.getUserId()) {
                is Result.Success -> result.data
                is Result.Error -> {
                    println("Не удалось получить user_id: ${describeError(result.error)}")
                    return@runBlocking
                }
            }
        } else {
            currentUserId = when (val result = api.getUserId()) {
                is Result.Success -> result.data
                is Result.Error -> {
                    println("Не удалось получить user_id: ${describeError(result.error)}")
                    return@runBlocking
                }
            }
        }

        println("user_id=$currentUserId")

        while (true) {
            printMenu(currentUserId)
            val choice = readlnOrNull()?.trim() ?: break
            if (choice.isEmpty()) continue

            println()
            try {
                when (choice) {
                    "0" -> break

                    "1" -> printResult("getUserId", api.getUserId()) { id ->
                        println("  user_id = $id")
                    }

                    "2" -> printResult("getProfileInfo", api.getProfileInfo()) { profile ->
                        println("  id=${profile.id}")
                        println("  name=${profile.firstName} ${profile.lastName}")
                        println("  screen_name=${profile.screenName ?: "—"}")
                    }

                    "3" -> {
                        print("owner_id (Enter = наш user_id $currentUserId): ")
                        val ownerId = readln().trim().ifEmpty { currentUserId.toString() }.toInt()

                        printResult("getUsersAudioCount", api.getUsersAudioCount(ownerId)) { count ->
                            println("  count = ${count.count}")
                        }
                    }

                    "4" -> {
                        print("owner_id (Enter = наш user_id $currentUserId): ")
                        val ownerId = readln().trim().ifEmpty { currentUserId.toString() }.toInt()

                        print("count (Enter = по умолчанию): ")
                        val count = readln().trim().toIntOrNull()

                        print("offset (Enter = по умолчанию): ")
                        val offset = readln().trim().toIntOrNull()

                        print("playlist_id (Enter = без плейлиста): ")
                        val playlistId = readln().trim().toIntOrNull()

                        val attributes = playlistId?.let {
                            print("access_key: ")
                            PlaylistAttributes(it, readln().trim())
                        }

                        printResult(
                            "getUsersAudio",
                            api.getUsersAudio(ownerId, count, offset, attributes)
                        ) { response ->
                            println("  count = ${response.count}")
                            printSongs(response.items)
                        }
                    }

                    "5" -> {
                        print("owner_id (Enter = наш user_id $currentUserId): ")
                        val ownerId = readln().trim().ifEmpty { currentUserId.toString() }.toInt()

                        print("count (Enter = по умолчанию): ")
                        val count = readln().trim().toIntOrNull()

                        print("offset (Enter = по умолчанию): ")
                        val offset = readln().trim().toIntOrNull()

                        printResult(
                            "getUsersPlaylist",
                            api.getUsersPlaylists(ownerId, count, offset)
                        ) { response ->
                            println("  count = ${response.count}")
                            response.nextFrom?.let { println("  next_from = $it") }
                            printPlaylists(response.items)
                        }
                    }

                    "6" -> {
                        print("query: ")
                        val query = readln()

                        print("count (Enter = по умолчанию): ")
                        val count = readln().trim().toIntOrNull()

                        print("offset (Enter = по умолчанию): ")
                        val offset = readln().trim().toIntOrNull()

                        printResult("searchAudio", api.searchAudio(query, count, offset)) { response ->
                            println("  count = ${response.count}")
                            printSongs(response.items)
                        }
                    }

                    "7" -> {
                        print("query: ")
                        val query = readln()

                        print("count (Enter = по умолчанию): ")
                        val count = readln().trim().toIntOrNull()

                        print("offset (Enter = по умолчанию): ")
                        val offset = readln().trim().toIntOrNull()

                        printResult("searchPlaylists", api.searchPlaylists(query, count, offset)) { response ->
                            println("  count = ${response.count}")
                            printPlaylists(response.items)
                        }
                    }

                    "8" -> {
                        print("owner_id (владелец ТРЕКА): ")
                        val ownerId = readln().toInt()

                        print("audio_id: ")
                        val audioId = readln().toInt()

                        printResult("addAudio", api.addAudio(ownerId, audioId)) { response ->
                            if (response.result == 0) {
                                println("  result = 0 (VK ничего не добавил)")
                            } else {
                                println("  new audio id = ${response.result}")
                            }
                        }
                    }

                    "9" -> {
                        print("owner_id (владелец ПЛЕЙЛИСТА, Enter = наш user_id $currentUserId): ")
                        val ownerId = readln().trim().ifEmpty { currentUserId.toString() }.toInt()

                        print("playlist_id: ")
                        val playlistId = readln().toInt()

                        print("audio_ids (как в списке: owner_id_audio_id, несколько через запятую): ")
                        val audioIds = readln()
                            .split(',')
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }

                        printResult(
                            "addAudiosToPlaylist",
                            api.addAudiosToPlaylist(ownerId, playlistId, audioIds)
                        ) { response ->
                            if (response.result.isEmpty()) {
                                println("  пустой список — VK ничего не добавил")
                            } else {
                                println("  добавлено: ${response.result.joinToString(", ") { it.audioId.toString() }}")
                            }
                        }
                    }

                    "10" -> {
                        print("owner_id (владелец ТРЕКА): ")
                        val ownerId = readln().toInt()

                        print("audio_id: ")
                        val audioId = readln().toInt()

                        printResult("deleteAudio", api.deleteAudio(ownerId, audioId)) { response ->
                            println("  result = ${response.result}")
                        }
                    }

                    "11" -> {
                        print("owner_id (владелец ПЛЕЙЛИСТА, Enter = наш user_id $currentUserId): ")
                        val ownerId = readln().trim().ifEmpty { currentUserId.toString() }.toInt()

                        print("playlist_id: ")
                        val playlistId = readln().toInt()

                        print("audio_ids (как в списке: owner_id_audio_id, несколько через запятую): ")
                        val audioIds = readln()
                            .split(',')
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }

                        printResult(
                            "removeAudiosFromPlaylist",
                            api.removeAudiosFromPlaylist(ownerId, playlistId, audioIds)
                        ) { response ->
                            println("  result = ${response.result}")
                        }
                    }

                    "12" -> {
                        print("owner_id (Enter = наш user_id $currentUserId): ")
                        val ownerId = readln().trim().ifEmpty { currentUserId.toString() }.toInt()

                        print("title: ")
                        val title = readln()

                        print("description (Enter = без описания): ")
                        val description = readln().trim().ifEmpty { null }

                        printResult("createPlaylist", api.createPlaylist(ownerId, title, description)) { response ->
                            println("  playlist_id = ${response.playlistId}")
                        }
                    }

                    "13" -> {
                        print("owner_id (Enter = наш user_id $currentUserId): ")
                        val ownerId = readln().trim().ifEmpty { currentUserId.toString() }.toInt()

                        print("playlist_id: ")
                        val playlistId = readln().toInt()

                        printResult("deletePlaylist", api.deletePlaylist(ownerId, playlistId)) { response ->
                            println("  result = ${response.result}")
                        }
                    }

                    "14" -> {
                        print("audio_ids (owner_id_audio_id, несколько через запятую): ")
                        val audioIds = readln()
                            .split(',')
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }

                        printResult("getById", api.getById(audioIds)) { response ->
                            printSongs(response.items)
                        }
                    }

                    "15" -> {
                        print("query: ")
                        val query = readln()

                        print("count (Enter = по умолчанию): ")
                        val count = readln().trim().toIntOrNull()

                        print("offset (Enter = по умолчанию): ")
                        val offset = readln().trim().toIntOrNull()

                        printResult("searchAlbums", api.searchAlbums(query, count, offset)) { response ->
                            println("  count = ${response.count}")
                            printPlaylists(response.items)
                        }
                    }

                    "16" -> {
                        print("count (Enter = по умолчанию): ")
                        val count = readln().trim().toIntOrNull()

                        print("offset (Enter = по умолчанию): ")
                        val offset = readln().trim().toIntOrNull()

                        printResult("getPopular", api.getPopular(count, offset)) { response ->
                            printSongs(response.items)
                        }
                    }

                    "17" -> {
                        tokenStorage.clearToken()

                        print("VK login: ")
                        val login = readln()

                        print("VK password: ")
                        val password = readln()

                        when (val authResult = auth.auth(login, password)) {
                            is Result.Error -> {
                                println("Ошибка авторизации: ${describeError(authResult.error)}")
                            }
                            is Result.Success -> {
                                when (val idResult = api.getUserId()) {
                                    is Result.Success -> {
                                        currentUserId = idResult.data
                                        println("Теперь вы вошли как user_id=$currentUserId")
                                    }
                                    is Result.Error -> println(
                                        "Вход выполнен, но user_id не получен: ${describeError(idResult.error)}"
                                    )
                                }
                            }
                        }
                    }

                    else -> println("Нет такого пункта: $choice")
                }
            } catch (throwable: Throwable) {
                println("Ошибка: ${throwable.message}")
            }

            println()
        }
    } finally {
        stopKoin()
    }
}

private fun printMenu(userId: Int) {
    print(
        """
        ═══════════════════════════════════════════════
         VK API console   user_id = $userId
        ═══════════════════════════════════════════════
          0) выход
          1) getUserId
          2) getProfileInfo
          3) getUsersAudioCount
          4) getUsersAudio
          5) getUsersPlaylist
          6) searchAudio
          7) searchPlaylists
          8) addAudio
          9) addAudiosToPlaylist
         10) deleteAudio
         11) removeAudiosFromPlaylist
         12) createPlaylist
         13) deletePlaylist
         14) getById
         15) searchAlbums
         16) getPopular
         17) войти заново
        Выберите пункт: 
        """.trimIndent() + " "
    )
}

private fun <T> printResult(
    name: String,
    result: Result<T, DataError.Remote>,
    printData: (T) -> Unit
) {
    when (result) {
        is Result.Success -> {
            println("$name:")
            printData(result.data)
        }
        is Result.Error -> println("$name: ${describeError(result.error)}")
    }
}

private fun printSongs(songs: List<SongResponse>) {
    if (songs.isEmpty()) {
        println("  (пусто)")
        return
    }
    println("  id (owner_id_audio_id) | artist — title | duration | access_key")
    songs.forEach { song ->
        println(
            "  ${song.ownerId}_${song.id} | ${song.artist} — ${song.title} | " +
                "${song.duration}s | access_key=${song.accessKey} | ${song.url}"
        )
    }
}

private fun printPlaylists(playlists: List<AlbumResponse>) {
    if (playlists.isEmpty()) {
        println("  (пусто)")
        return
    }
    println("  playlist (owner_id_playlist_id) | title | count | access_key")
    playlists.forEach { playlist ->
        println(
            "  ${playlist.ownerId}_${playlist.id} | ${playlist.title} | " +
                "${playlist.count} | access_key=${playlist.accessKey}"
        )
    }
}

private fun describeError(error: DataError): String = when (error) {
    is DataError.Remote.Api -> when (val apiError = error.error) {
        is VkApiError.CodeError -> "VK error ${apiError.code}: ${apiError.message}"
        is VkApiError.TextError -> buildString {
            append("VK ")
            append(apiError.type)
            apiError.description?.let { append(": ").append(it) }
        }
        is VkApiError.UnknownError -> "VK unknown: ${apiError.json}"
    }
    DataError.Remote.NotAuthorized -> "не авторизован"
    DataError.Remote.NoInternet -> "нет интернета"
    DataError.Remote.RequestTimeout -> "таймаут запроса"
    DataError.Remote.TooManyRequests -> "слишком много запросов"
    DataError.Remote.Server -> "ошибка сервера"
    DataError.Remote.Serialization -> "ошибка разбора ответа"
    DataError.Remote.Unknown -> "неизвестная ошибка"
    is DataError.Remote.CaptchaFailed -> "капча: ${error.msg}"
    is DataError.Local.BrowserNotFound -> "браузер не найден: ${error.msg}"
}
