package dev.lcdii.kmp_vk_music_api.components.responses.audio

import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.PhotoResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.SongAlbumResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.SongArtistResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.SongResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.VkApiResponse
import kotlin.time.Instant

val EXPECTED_SEARCH = VkApiResponse.AudioSearch(
    count = 1,
    items = listOf(
        SongResponse(
            id = 456948858,
            ownerId = 474499195,
            title = "ЭКСПОНАТ",
            artist = "MIA BOYKA",
            duration = 99,
            accessKey = "b1d33746750e1149f1",
            isExplicit = false,
            isFocusTrack = false,
            isLicensed = true,
            trackCode = "53b40d21mCx8EeWCZLGZnTgHpvBQIY63E1v8HAvra5AqK_hbRTuzKg90hPAOrPfxgIs-cduoldWyPApVJRxRX4fAKO72",
            date = Instant.fromEpochSeconds(1778163881),
            url = "https://cs2-15v4.vkuseraudio.net/s/v1/ac/3hDHAkJanB9xnDI_BfNnmbhPNOV8H44E4BLAE7bPhzYS9OHAWpm8h2RbJuNVZjJQPtjOWUlcZfk6n80yZREvaqDrBlrSjgs1z9gJeGxYwvNH2PnWJuSF-IiKxXoZxef_UZXuoG8K8QeVihuaBJrc-IOg7n628XfdfR3LgFsNBn92_DQ/index.m3u8?siren=1",
            album = SongAlbumResponse(
                id = 27632388,
                title = "unnamed",
                ownerId = -2000632388,
                accessKey = "da248a4e1c30bafe88",
                thumb = PhotoResponse(
                    width = 1500,
                    height = 1500,
                    id = "457239048",
                    photo34url = "https://sun2-15.userapi.com/impg/PXsiUtv24B30YboyAlZcsij4AbU4U0-xwYZENA/-fBnwIsQKF8.jpg?size=34x34&quality=95&sign=2598d69ba7e702318f6ff639464acd5c&c_uniq_tag=pZlxJYyvDW-17SfqnWOrJhwAwquDvJyZC9dPSTgliZQ&type=audio",
                    photo68url = "https://sun2-15.userapi.com/impg/PXsiUtv24B30YboyAlZcsij4AbU4U0-xwYZENA/-fBnwIsQKF8.jpg?size=68x68&quality=95&sign=efe0ed05763c6087bbc5e2345eb8dc4a&c_uniq_tag=r7NAImCdGmKIAUDe2UwFMQ9A5_Ar3648z6R6chywPLU&type=audio",
                    photo135url = "https://sun2-15.userapi.com/impg/PXsiUtv24B30YboyAlZcsij4AbU4U0-xwYZENA/-fBnwIsQKF8.jpg?size=135x135&quality=95&sign=1c4d13a8ffaecd0b5e34cd32ce37af82&c_uniq_tag=Q3RwgKN0gGMYvcRHAreaJweGcxU6gjCOWHEYO7Qa3dI&type=audio",
                    photo270url = "https://sun2-15.userapi.com/impg/PXsiUtv24B30YboyAlZcsij4AbU4U0-xwYZENA/-fBnwIsQKF8.jpg?size=270x270&quality=95&sign=e84891bf5bb6b29282d43f46b9027463&c_uniq_tag=5P3RIM27FXWkP8_-vJHh-D4Xko8V9pZqbxcEJuhLKF0&type=audio",
                    photo300url = "https://sun2-15.userapi.com/impg/PXsiUtv24B30YboyAlZcsij4AbU4U0-xwYZENA/-fBnwIsQKF8.jpg?size=300x300&quality=95&sign=47ec78cbb05ea98e1e77524e28993119&c_uniq_tag=n87FOB0iLFsXmQlikaPH5UVXjW7rhF-1Ll-zdZyXgZc&type=audio",
                    photo600url = "https://sun2-15.userapi.com/impg/PXsiUtv24B30YboyAlZcsij4AbU4U0-xwYZENA/-fBnwIsQKF8.jpg?size=600x600&quality=95&sign=8ec9c6bcdf44f7614355f58173dcb412&c_uniq_tag=LbL8jGCCwJSwaG7VRbqoJtI6y5Sx0_gYg8LNvDQO2wA&type=audio",
                    photo1200url = "https://sun2-15.userapi.com/impg/PXsiUtv24B30YboyAlZcsij4AbU4U0-xwYZENA/-fBnwIsQKF8.jpg?size=1200x1200&quality=95&sign=159a2976c4e9ac0afbab6b720432ab04&c_uniq_tag=qS06UUYhqmqymHSXXd_KQqd0BK4y2XhGs7jsdIPBwno&type=audio",
                ),
                mainColor = "#FFFFFF",
            ),
            trackId = 1303419376,
            fileId = "0b2f9ecf-1c25-4591-ba60-f01a2feec24d",
            lyricsId = 1,
            mainArtists = listOf(
                SongArtistResponse(
                    name = "MIA BOYKA",
                    domain = "6698891307373897747",
                    id = "6698891307373897747",
                )
            ),
            isOfficial = true,
            releaseAudioId = "-2001444231_149444231",
        )
    )
)