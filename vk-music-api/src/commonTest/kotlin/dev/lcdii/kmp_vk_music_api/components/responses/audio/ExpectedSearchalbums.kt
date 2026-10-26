package dev.lcdii.kmp_vk_music_api.components.responses.audio

import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.AlbumResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.GenreResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.PermissionsResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.PhotoResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.SongArtistResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.VkApiResponse
import kotlin.time.Instant

val EXPECTED_SEARCH_ALBUMS = VkApiResponse.AudioSearchAlbums(
    count = 1,
    items = listOf(
        AlbumResponse(
            id = 3323556,
            ownerId = -2000323556,
            type = 1,
            title = "Rock",
            description = "",
            count = 81,
            followers = 7163,
            plays = 684053,
            createTime = Instant.fromEpochSeconds(1533036700),
            updateTime = Instant.fromEpochSeconds(1633109452),
            genres = listOf(
                GenreResponse(id = 2, name = "Рок")
            ),
            isFollowing = false,
            photo = PhotoResponse(
                width = 1500,
                height = 1500,
                id = "457239045",
                photo34url = "https://sun1-85.userapi.com/impg/SXb2Iuar6h1cNA-kQL-jctYGd1t9oVamk_BWlA/cYKWHdC2chc.jpg?size=34x34&quality=95&sign=f71ad4f2b8d4ff8f5109a98a924abde5&c_uniq_tag=eZcEdbwkBO3ENomKf_OIFlcMJiakCspDoht7UZ42CTE&type=audio",
                photo68url = "https://sun1-85.userapi.com/impg/SXb2Iuar6h1cNA-kQL-jctYGd1t9oVamk_BWlA/cYKWHdC2chc.jpg?size=68x68&quality=95&sign=e05744f8571fdbd454bbdfe12e585439&c_uniq_tag=SqOc2eDwl8lNP2zz39Cv4nbZfqZxEoeyoMM4LNSPBow&type=audio",
                photo135url = "https://sun1-85.userapi.com/impg/SXb2Iuar6h1cNA-kQL-jctYGd1t9oVamk_BWlA/cYKWHdC2chc.jpg?size=135x135&quality=95&sign=a0cea3a8fadfe437ff83fd25a83c66f0&c_uniq_tag=iENkgCdFdtM-yR83-c3RKZtvT_ueqLs0wOSpdsOjNYo&type=audio",
                photo270url = "https://sun1-85.userapi.com/impg/SXb2Iuar6h1cNA-kQL-jctYGd1t9oVamk_BWlA/cYKWHdC2chc.jpg?size=270x270&quality=95&sign=5d374ec1eedf09cf371bf9a921f43b31&c_uniq_tag=CtT9aL1bjZHYAtqZUQ4t-ynyy2vlcUC6gjkxkK6fPUA&type=audio",
                photo300url = "https://sun1-85.userapi.com/impg/SXb2Iuar6h1cNA-kQL-jctYGd1t9oVamk_BWlA/cYKWHdC2chc.jpg?size=300x300&quality=95&sign=4a907804fd40d6fcc3a7f55e1337e000&c_uniq_tag=6d8LuFSCUU_SHWon-wFcS41spt0hyzvbiPX4hkiVt7s&type=audio",
                photo600url = "https://sun1-85.userapi.com/impg/SXb2Iuar6h1cNA-kQL-jctYGd1t9oVamk_BWlA/cYKWHdC2chc.jpg?size=600x600&quality=95&sign=45c793448a2e9917e353823d7b426e49&c_uniq_tag=6nqHVNvjr-e7W0XuqrhjrMpD4vMKdseaso0MkUff_P4&type=audio",
                photo1200url = "https://sun1-85.userapi.com/impg/SXb2Iuar6h1cNA-kQL-jctYGd1t9oVamk_BWlA/cYKWHdC2chc.jpg?size=1200x1200&quality=95&sign=6525980a585fc00eb491521f0ecb77a2&c_uniq_tag=HsoKSB4gNhwLAM7PMOnbJPMvxAtyawMxyuUQpk7UW5M&type=audio",
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
            accessKey = "d83cf97d17fbe842a2",
            albumType = "collection",
            isExplicit = true,
            mainColor = "#ED1B26",
            year = 2018,
            mainArtists = listOf(
                SongArtistResponse(name = "Various Artists")
            ),
        )
    ),
)