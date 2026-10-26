package dev.lcdii.kmp_vk_music_api.components.responses.audio

import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.AlbumResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.PermissionsResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.PhotoResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.VkApiResponse
import kotlin.collections.emptyList
import kotlin.time.Instant

val EXPECTED_SEARCH_PLAYLISTS = VkApiResponse.AudioSearchPlaylists(
    count = 1,
    items = listOf(
        AlbumResponse(
            id = 1776,
            ownerId = -147845620,
            type = 0,
            title = "Беззаботный рок",
            description = "",
            count = 25,
            followers = 7081,
            plays = 731400,
            createTime = Instant.fromEpochSeconds(1576788783),
            updateTime = Instant.fromEpochSeconds(1694799063),
            genres = emptyList(),
            isFollowing = false,
            photo = PhotoResponse(
                width = 1080,
                height = 1080,
                id = "457383197",
                photo34url = "https://sun2-19.userapi.com/impg/bI02M5lsTLT5ObtNgG8ByezKxoqr-002wW9JpA/if_yi4FxUVY.jpg?size=34x34&quality=95&sign=0b570ce7e7b5d948b904ccc53145d6ca&c_uniq_tag=k34d3CjMIm-Wa8KqjqtkH__94VWDzU1OzpZl0v2MMXg&type=audio",
                photo68url = "https://sun2-19.userapi.com/impg/bI02M5lsTLT5ObtNgG8ByezKxoqr-002wW9JpA/if_yi4FxUVY.jpg?size=68x68&quality=95&sign=b0cd36d71cf7d12fbc39e02ab265a612&c_uniq_tag=VyEx-LixvRCfXRedxB2VT-MmuqCpAHs2RL6ed10quIk&type=audio",
                photo135url = "https://sun2-19.userapi.com/impg/bI02M5lsTLT5ObtNgG8ByezKxoqr-002wW9JpA/if_yi4FxUVY.jpg?size=135x135&quality=95&sign=8714d5f797cbda56e981b283f9152438&c_uniq_tag=bEwDRvPNOXvEOP5eedLAfayjpNCFyvnRNrbtdBq4OEU&type=audio",
                photo270url = "https://sun2-19.userapi.com/impg/bI02M5lsTLT5ObtNgG8ByezKxoqr-002wW9JpA/if_yi4FxUVY.jpg?size=270x270&quality=95&sign=2d1903560d42ee8a421822d3fc8713a6&c_uniq_tag=oZjkev2hyZmZKOOZPDVEbABNdreA-aCsB33VpDivDNI&type=audio",
                photo300url = "https://sun2-19.userapi.com/impg/bI02M5lsTLT5ObtNgG8ByezKxoqr-002wW9JpA/if_yi4FxUVY.jpg?size=300x300&quality=95&sign=f12a442f97265dea55a43a836a1b0a55&c_uniq_tag=QxxMjhjuVIn-Zc6MzRD6wFJPolL8DpRh4bziaWN0jBs&type=audio",
                photo600url = "https://sun2-19.userapi.com/impg/bI02M5lsTLT5ObtNgG8ByezKxoqr-002wW9JpA/if_yi4FxUVY.jpg?size=600x600&quality=95&sign=16dbea145860d4ed7f9911d588c99125&c_uniq_tag=hSdhAqGY_vAuktr3gkxs6GJ04QpHXrxbpoh5bue3xJo&type=audio",
                photo1200url = "https://sun2-19.userapi.com/impg/bI02M5lsTLT5ObtNgG8ByezKxoqr-002wW9JpA/if_yi4FxUVY.jpg?size=1080x1080&quality=95&sign=bdf2d5e620d940b5da0f228e2980daf3&c_uniq_tag=7QxtXX7enc4kkiYH0L-A71JoF9q1JyGSXxpdA90mcd4&type=audio",
            ),
            permissions = PermissionsResponse(
                play = true,
                share = true,
                edit = false,
                follow = true,
                delete = false,
                boomDownload = true,
            ),
            subtitleBadge = false,
            playButton = false,
            accessKey = "ce1351afea67c5ee60",
            albumType = "playlist",
            isExplicit = null,
            mainColor = "#F58770",
            year = null,
            mainArtists = null,
        )
    )
)