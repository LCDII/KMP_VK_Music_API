package dev.lcdii.kmp_vk_music_api.vk_api

import kotlinx.serialization.Serializable
import kotlin.time.Instant

//NOT ful responses
@Serializable
data class PhotoResponse(
    val width: Int,
    val height: Int,
    val id: Int,
    val photo34url: String,
    val photo68url: String,
    val photo135url: String,
    val photo270url: String,
    val photo300url: String,
    val photo600url: String,
    val photo1200url: String,
)
@Serializable
data class SongAlbumResponse(
    val id: Int,
    val title: String,
    val ownerId: Int,
    val accessKey: String,
    val thumb: PhotoResponse,
    val mainColor: String,
)
@Serializable
data class SongArtistResponse(
    val name: String,
    val domain: String? = null,
    val id: String? = null
)
@Serializable
data class SongResponse(
    val id : Int,
    val ownerId: Int,
    val title: String,
    val artist: String,
    val duration: Int,
    val accessKey: String,
    val isExplicit: Boolean,
    val isFocusTrack: Boolean,
    val isLicensed: Boolean,
    //custom
    val date: Instant,
    val url: String,
    val album: SongAlbumResponse? = null,
    val trackId: Int,
    val fileId: String,
    val lyricsId: Int? = null,
    val mainArtists: List<SongArtistResponse>? = null,
    val isOfficial: Boolean,
    val releaseAudioId: String
)
@Serializable
data class PermissionsResponse(
    val play: Boolean,
    val share: Boolean,
    val edit: Boolean,
    val follow: Boolean,
    val delete: Boolean,
    val boomDownload: Boolean
)
@Serializable
data class GenreResponse(
    val id: Int,
    val name: String
)
@Serializable
data class AlbumResponse(
    val id: Int,
    val ownerId: Int,
    val type: Int,
    val title: String,
    val description: String,
    val count: Int,
    val followers: Int,
    val plays: Int,
    val createTime: Instant,
    val updateTime: Instant,
    val genres: List<GenreResponse> = emptyList(),
    val isFollowing: Boolean,
    val photo: PhotoResponse,
    val permissions: PermissionsResponse,
    val subtitleBadge: Boolean,
    val playButton: Boolean,
    val accessKey: String,
    val albumType: String,
    val isExplicit: Boolean? = null,
    val mainColor: String? = null,
    val year: String? = null,
    val mainArtists: List<SongArtistResponse>? = null
    )
sealed interface VkApiResponse {
    data class AudioGetCount(
        val count : Int,
    ) : VkApiResponse

    data class AudioGet(
        val count: Int,
        val items: List<SongResponse> = emptyList()
    ) : VkApiResponse

    data class AudioSearch(
        val count: Int,
        val items: List<SongResponse> = emptyList()
    ) : VkApiResponse

    data class AudioGetById(
        val items: List<SongResponse> = emptyList()
    )

    data class AudioGetPlayLists(
        val count: Int,
        val items: List<AlbumResponse> = emptyList(),
        val nextFrom: String? = null
        //TODO profiles
    )

    data class AudioSearchPlaylists(
        val count: Int,
        val items: List<AlbumResponse> = emptyList(),

    )

    data class AudioSearchAlbums(
        val count: Int,
        val items: List<AlbumResponse>
    )

}