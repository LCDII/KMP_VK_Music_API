package dev.lcdii.kmp_vk_music_api.vk_api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.time.Instant

//NOT ful responses
@Serializable
data class PhotoResponse(
    val width: Int,
    val height: Int,
    val id: String,
    @SerialName("photo_34")
    val photo34url: String,
    @SerialName("photo_68")
    val photo68url: String,
    @SerialName("photo_135")
    val photo135url: String,
    @SerialName("photo_270")
    val photo270url: String,
    @SerialName("photo_300")
    val photo300url: String,
    @SerialName("photo_600")
    val photo600url: String,
    @SerialName("photo_1200")
    val photo1200url: String,
)

@Serializable
data class CityResponse(
    val id: Int,
    val title: String
)

@Serializable
data class CountryResponse(
    val id: Int,
    val title: String
)

@Serializable
data class SongAlbumResponse(
    val id: Int,
    val title: String,
    @SerialName("owner_id")
    val ownerId: Int,
    @SerialName("access_key")
    val accessKey: String,
    val thumb: PhotoResponse,
    @SerialName("main_color")
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
    @SerialName("owner_id")
    val ownerId: Int,
    val title: String,
    val artist: String,
    val duration: Int,
    @SerialName("access_key")
    val accessKey: String,
    @SerialName("is_explicit")
    val isExplicit: Boolean,
    @SerialName("is_focus_track")
    val isFocusTrack: Boolean,
    @SerialName("is_licensed")
    val isLicensed: Boolean,
    @SerialName("track_code")
    val trackCode: String,
    @SerialName("genre_id")
    val genreId: Int? = null,
    val like: Boolean? = null,

    //custom
    @Serializable(with = InstantSerializer::class)
    val date: Instant,
    val url: String,
    val album: SongAlbumResponse? = null,
    @SerialName("track_id")
    val trackId: Int? = null,
    @SerialName("file_id")
    val fileId: String? = null,
    @SerialName("lyrics_id")
    val lyricsId: Int? = null,
    @SerialName("main_artists")
    val mainArtists: List<SongArtistResponse>? = null,
    @SerialName("is_official")
    val isOfficial: Boolean = false,
    @SerialName("release_audio_id")
    val releaseAudioId: String? = null
) {
    object InstantSerializer : KSerializer<Instant>{
        override val descriptor = PrimitiveSerialDescriptor("InstantFromSeconds",
            PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder): Instant =
            Instant.fromEpochSeconds(decoder.decodeLong())

        override fun serialize(encoder: Encoder, value: Instant) {
            encoder.encodeLong(value.epochSeconds)
        }
    }
}
@Serializable
data class PermissionsResponse(
    val play: Boolean,
    val share: Boolean,
    val edit: Boolean,
    val follow: Boolean,
    val delete: Boolean,
    @SerialName("boom_download")
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
    @SerialName("owner_id")
    val ownerId: Int,
    val type: Int,
    val title: String,
    val description: String,
    val count: Int,
    val followers: Int,
    val plays: Int,
    @Serializable(with = InstantSerializer::class)
    @SerialName("create_time")
    val createTime: Instant,
    @Serializable(with = InstantSerializer::class)
    @SerialName("update_time")
    val updateTime: Instant,
    val genres: List<GenreResponse> = emptyList(),
    @SerialName("is_following")
    val isFollowing: Boolean,
    val photo: PhotoResponse,
    val permissions: PermissionsResponse,
    @SerialName("subtitle_badge")
    val subtitleBadge: Boolean,
    @SerialName("play_button")
    val playButton: Boolean,
    @SerialName("access_key")
    val accessKey: String,
    @SerialName("album_type")
    val albumType: String,
    @SerialName("is_explicit")
    val isExplicit: Boolean? = null,
    @SerialName("main_color")
    val mainColor: String? = null,
    val year: Int? = null,
    @SerialName("main_artists")
    val mainArtists: List<SongArtistResponse>? = null
    ) {
        object InstantSerializer : KSerializer<Instant>{
            override val descriptor = PrimitiveSerialDescriptor("InstantFromSeconds",
                PrimitiveKind.LONG)

            override fun deserialize(decoder: Decoder): Instant =
                Instant.fromEpochSeconds(decoder.decodeLong())

            override fun serialize(encoder: Encoder, value: Instant) {
                encoder.encodeLong(value.epochSeconds)
            }
        }
}

sealed interface VkApiResponse: ApiResponse {
    @Serializable
    data class ProfileInfo(
        val id: Int,

        @SerialName("first_name")
        val firstName: String,

        @SerialName("last_name")
        val lastName: String,

        @SerialName("screen_name")
        val screenName: String? = null,

        val sex: Int = 0,
        val bdate: String? = null,
        val status: String? = null,

        @SerialName("photo_200")
        val photo200: String? = null,

        val city: CityResponse? = null,
        val country: CountryResponse? = null,

        @SerialName("is_verified")
        val isVerified: Boolean = false
    ) : VkApiResponse

    //Audio Responses
    @Serializable(with= AudioGetCount.CountSerializer::class)
    data class AudioGetCount(
        val count : Int,
    ) : VkApiResponse{
        object CountSerializer: KSerializer<VkApiResponse.AudioGetCount>{
            override val descriptor = buildClassSerialDescriptor("AudioGetCount") {
                element<Int>("count")
            }

            override fun serialize(
                encoder: Encoder,
                value: AudioGetCount
            ) = encoder.encodeInt(value.count)

            override fun deserialize(decoder: Decoder): AudioGetCount =
                AudioGetCount(decoder.decodeInt())

        }
    }
    @Serializable
    data class AudioGet(
        val count: Int,
        val items: List<SongResponse> = emptyList()
        //TODO profiles
    ) : VkApiResponse
    @Serializable
    data class AudioSearch(
        val count: Int,
        val items: List<SongResponse> = emptyList()
    ) : VkApiResponse
    @Serializable(with =  AudioGetById.AudioGetByIdSerializer::class)
    data class AudioGetById(
        val items: List<SongResponse> = emptyList()
    ) : VkApiResponse {
        object AudioGetByIdSerializer : KSerializer<VkApiResponse.AudioGetById> {
            override val descriptor = ListSerializer(SongResponse.serializer()).descriptor

            override fun deserialize(decoder: Decoder): VkApiResponse.AudioGetById {
                val list =
                    decoder.decodeSerializableValue(ListSerializer(SongResponse.serializer()))
                return VkApiResponse.AudioGetById(items = list)
            }

            override fun serialize(encoder: Encoder, value: VkApiResponse.AudioGetById) {
                encoder.encodeSerializableValue(
                    ListSerializer(SongResponse.serializer()),
                    value.items
                )
            }
        }
    }
    @Serializable
    data class AudioGetPlayLists(
        val count: Int,
        val items: List<AlbumResponse> = emptyList(),
        @SerialName("next_from")
        val nextFrom: String? = null
        //TODO profiles
    ) : VkApiResponse{}

    @Serializable
    data class AudioSearchPlaylists(
        val count: Int,
        val items: List<AlbumResponse> = emptyList(),

    ) : VkApiResponse
    @Serializable
    data class AudioSearchAlbums(
        val count: Int,
        val items: List<AlbumResponse>
    ) : VkApiResponse

}

inline fun <reified T: VkApiResponse> Json.decodeResponse(jsonString: String): T =
    this.decodeFromJsonElement<T>(
        this.parseToJsonElement(jsonString).jsonObject["response"] ?:
        error("No Response Object")
    )