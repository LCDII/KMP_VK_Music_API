package dev.lcdii.kmp_vk_music_api.components.responses.audio

import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.PhotoResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.SongAlbumResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.SongArtistResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.SongResponse
import dev.lcdii.kmp_vk_music_api.vk_api.data.dto.VkApiResponse
import kotlin.time.Instant

val EXPECTED_GET_BY_ID = VkApiResponse.AudioGetById(
    items = listOf(
        SongResponse(
            id = 456671411,
            ownerId = 474499162,
            title = "Отпускай",
            artist = "Три дня дождя",
            duration = 207,
            accessKey = "abb664e5c2ba9a8c96",
            isExplicit = false,
            isFocusTrack = false,
            isLicensed = true,
            trackCode = "d332ca485jT7N6h2Vu_CiB9a5P7LUChQEJNR5eWbqSV-AuWlLKaS6Q_zLBaQAldRHlpiCXiG",
            date = Instant.fromEpochSeconds(1650461357),
            url = "https://cs2-9v4.vkuseraudio.net/s/v1/ac/S0Lg41FB6e8oM4-CZwWnqVRrnbRd926ovFGPNjFB0LSlOBQQemi0NqFRNrvzUop5oCxJyoHxS2ktKkXXFIMEvtC4mGA2iig-yT5J6iq8hhUfupwNmTE0X9mbtArXTyheS5PSTe2LaOB773ZebtJfQ8MosRFPl-37T5svFlKCg7jxl7Y/index.m3u8?siren=1",
            album = SongAlbumResponse(
                id = 21123977,
                title = "unnamed",
                ownerId = -2000123977,
                accessKey = "34a91de07de877420b",
                thumb = PhotoResponse(
                    width = 1500,
                    height = 1500,
                    id = "457239047",
                    photo34url = "https://sun1-15.userapi.com/impg/GdbMHNhU_BBh2mbtYLIZ_uonEZxObiVEsaEgAA/Rnf4jCW4Pwc.jpg?size=34x34&quality=95&sign=e76d5e88b020fe53a5b63a2f3f533922&c_uniq_tag=C6HXlfyLtBaAZo7gwTOk34zf8JbgLemMPUaZWiGCk8I&type=audio",
                    photo68url = "https://sun1-15.userapi.com/impg/GdbMHNhU_BBh2mbtYLIZ_uonEZxObiVEsaEgAA/Rnf4jCW4Pwc.jpg?size=68x68&quality=95&sign=b39baedee3eb699a7377ce91c6762633&c_uniq_tag=WeuV92fdIB8lf_k8kfvqsucruX2Tbndy2wwJVwsKF44&type=audio",
                    photo135url = "https://sun1-15.userapi.com/impg/GdbMHNhU_BBh2mbtYLIZ_uonEZxObiVEsaEgAA/Rnf4jCW4Pwc.jpg?size=135x135&quality=95&sign=b2f4fc355f76a8d79a6ffb16034ff72d&c_uniq_tag=uYs3o3yBFEzNr1GifS-3Xo-ADQxm10YtMD1GOLICgCs&type=audio",
                    photo270url = "https://sun1-15.userapi.com/impg/GdbMHNhU_BBh2mbtYLIZ_uonEZxObiVEsaEgAA/Rnf4jCW4Pwc.jpg?size=270x270&quality=95&sign=70adcb2cd92e662a57d4b4ec5ab5985e&c_uniq_tag=guL9zY6OWPmm5z6j51dMFZRVIQ6XK7WHoICOFnDjfz0&type=audio",
                    photo300url = "https://sun1-15.userapi.com/impg/GdbMHNhU_BBh2mbtYLIZ_uonEZxObiVEsaEgAA/Rnf4jCW4Pwc.jpg?size=300x300&quality=95&sign=66d6b589e5d4a674a307aff85f22e2d2&c_uniq_tag=o9SxyrLlOjQ4s_K6nnuFtMICVPAN35008k96MeXTpHg&type=audio",
                    photo600url = "https://sun1-15.userapi.com/impg/GdbMHNhU_BBh2mbtYLIZ_uonEZxObiVEsaEgAA/Rnf4jCW4Pwc.jpg?size=600x600&quality=95&sign=0c2614dffd5dc57678ba5b45ec5d8b6d&c_uniq_tag=a4FkgH40uBoE6DNENRlLDYvDBaFZAOLzOEx-IX6JCBI&type=audio",
                    photo1200url = "https://sun1-15.userapi.com/impg/GdbMHNhU_BBh2mbtYLIZ_uonEZxObiVEsaEgAA/Rnf4jCW4Pwc.jpg?size=1200x1200&quality=95&sign=ff2333ee751d24fdb1cd3e0f546e6ffb&c_uniq_tag=7bZY-PewK3k1cJzzK8hZtD6bjnN1p-Y0cAWf3KSqJvw&type=audio",
                ),
                mainColor = "#030303",
            ),
            trackId = 62253391,
            fileId = "ac40ecab-2aea-4428-9f5e-def5a3e9f71e",
            lyricsId = 1,
            mainArtists = listOf(
                SongArtistResponse(
                    name = "Три дня дождя",
                    domain = "5428185118660812105",
                    id = "5428185118660812105",
                )
            ),
            isOfficial = true,
            releaseAudioId = "-2001500678_128500678",
        )
    ),
)