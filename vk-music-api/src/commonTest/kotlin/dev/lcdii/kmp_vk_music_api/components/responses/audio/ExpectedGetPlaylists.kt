package dev.lcdii.kmp_vk_music_api.components.responses.audio

import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.AlbumResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.PermissionsResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.PhotoResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.VkApiResponse
import kotlin.time.Instant

val EXPECTED_GET_PLAYLISTS = VkApiResponse.AudioGetPlayLists(
    count = 1,
    items = listOf(
        AlbumResponse(
            id = 11,
            ownerId = 336591731,
            type = 0,
            title = "Мне нравится",
            description = "",
            count = 398,
            followers = 0,
            plays = 0,
            createTime = Instant.fromEpochSeconds(1785875519),
            updateTime = Instant.fromEpochSeconds(1785875523),
            genres = emptyList(),
            isFollowing = false,
            photo = PhotoResponse(
                width = 300,
                height = 300,
                id = "457249588",
                photo34url = "https://sun1-56.userapi.com/impg/lWLGWUMctycnAE1gHbWR2rjkSajetFKKEBsVPw/i3fAUlMHqms.jpg?size=34x34&quality=95&sign=03a773c6b6c56eeba02edb9cb4c6a948&c_uniq_tag=HREi9jgsYOMh8EUFvNKXJyVWGSS82Vk-VytnreHsOgw&type=audio",
                photo68url = "https://sun1-56.userapi.com/impg/lWLGWUMctycnAE1gHbWR2rjkSajetFKKEBsVPw/i3fAUlMHqms.jpg?size=68x68&quality=95&sign=643c9398b19161b2fda78b47cc52b1e8&c_uniq_tag=hPVqucWXXt3L9gn_hwqBnTACM__Dzm4hdLVumNYroF4&type=audio",
                photo135url = "https://sun1-56.userapi.com/impg/lWLGWUMctycnAE1gHbWR2rjkSajetFKKEBsVPw/i3fAUlMHqms.jpg?size=135x135&quality=95&sign=aa0368206e9f428c26d5aa3c366422e6&c_uniq_tag=PF5d9jdb3lcvVyWsvzToViSWi25BcC3bFwxj8c-KsLo&type=audio",
                photo270url = "https://sun1-56.userapi.com/impg/lWLGWUMctycnAE1gHbWR2rjkSajetFKKEBsVPw/i3fAUlMHqms.jpg?size=270x270&quality=95&sign=70b49303c397140d891cd6422eb025d1&c_uniq_tag=CgRwKJjQXQM_E6fWGvroDBNTqhgLuC7kZ7bzpqUcH30&type=audio",
                photo300url = "https://sun1-56.userapi.com/impg/lWLGWUMctycnAE1gHbWR2rjkSajetFKKEBsVPw/i3fAUlMHqms.jpg?size=300x300&quality=95&sign=117b08e54ebc7ab5c87c07c59c59a19e&c_uniq_tag=utgEkFyMhNaXBId2-7eo_y6JkNwn3L9wsgblSQ5aokE&type=audio",
                photo600url = "https://sun1-56.userapi.com/impg/lWLGWUMctycnAE1gHbWR2rjkSajetFKKEBsVPw/i3fAUlMHqms.jpg?size=300x300&quality=95&sign=117b08e54ebc7ab5c87c07c59c59a19e&c_uniq_tag=utgEkFyMhNaXBId2-7eo_y6JkNwn3L9wsgblSQ5aokE&type=audio",
                photo1200url = "https://sun1-56.userapi.com/impg/lWLGWUMctycnAE1gHbWR2rjkSajetFKKEBsVPw/i3fAUlMHqms.jpg?size=300x300&quality=95&sign=117b08e54ebc7ab5c87c07c59c59a19e&c_uniq_tag=utgEkFyMhNaXBId2-7eo_y6JkNwn3L9wsgblSQ5aokE&type=audio",
            ),
            permissions = PermissionsResponse(
                play = true,
                share = true,
                edit = true,
                follow = false,
                delete = true,
                boomDownload = true,
            ),
            subtitleBadge = false,
            playButton = false,
            accessKey = "35008044331119776f",
            albumType = "playlist",
            isExplicit = null,
            mainColor = null,
            year = null,
            mainArtists = null,
        )
    ),
    nextFrom = "PUlYVA8FFg",
)