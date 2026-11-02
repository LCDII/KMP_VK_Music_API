package dev.lcdii.kmp_vk_music_api.vk_api.data.dto

import dev.lcdii.kmp_vk_music_api.vk_api.domain.ApiResponse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.time.Instant

//NOT ful responses
@Serializable
public data class PhotoResponse(
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
public data class CityResponse(
    val id: Int,
    val title: String
)

@Serializable
public data class CountryResponse(
    val id: Int,
    val title: String
)

@Serializable
public data class SongAlbumResponse(
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
public data class SongArtistResponse(
    val name: String,
    val domain: String? = null,
    val id: String? = null
)
@Serializable
public data class SongResponse(
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
    public object InstantSerializer : KSerializer<Instant>{
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("InstantFromSeconds",
            PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder): Instant =
            Instant.fromEpochSeconds(decoder.decodeLong())

        override fun serialize(encoder: Encoder, value: Instant) {
            encoder.encodeLong(value.epochSeconds)
        }
    }
}
@Serializable
public data class PermissionsResponse(
    val play: Boolean,
    val share: Boolean,
    val edit: Boolean,
    val follow: Boolean,
    val delete: Boolean,
    @SerialName("boom_download")
    val boomDownload: Boolean
)
@Serializable
public data class GenreResponse(
    val id: Int,
    val name: String
)
@Serializable
public data class AlbumResponse(
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
    val photo: PhotoResponse? = null,
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
        public object InstantSerializer : KSerializer<Instant>{
            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("InstantFromSeconds",
                PrimitiveKind.LONG)

            override fun deserialize(decoder: Decoder): Instant =
                Instant.fromEpochSeconds(decoder.decodeLong())

            override fun serialize(encoder: Encoder, value: Instant) {
                encoder.encodeLong(value.epochSeconds)
            }
        }
}

public abstract class IntResponseSerializer<T>(
    private val serialName: String,
    private val fromInt: (Int) -> T,
    private val toInt: (T) -> Int
) : KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName,
        PrimitiveKind.INT
    )

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeInt(toInt(value))
    }

    override fun deserialize(decoder: Decoder): T = fromInt(decoder.decodeInt())
}

public abstract class ListResponseSerializer<T, R>( //for response : []
    private val itemSerializer: KSerializer<R>,
    private val fromList: (List<R>) -> T,
    private val toList: (T) -> List<R>
) : KSerializer<T> {
    private val listSerializer = ListSerializer(itemSerializer)

    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeSerializableValue(listSerializer, toList(value))
    }

    override fun deserialize(decoder: Decoder): T = fromList(
        decoder.decodeSerializableValue(listSerializer)
    )
}

public sealed interface VkApiResponse: ApiResponse {
    @Serializable
    public data class AuthResponse(
        @SerialName("user_id")
        val userId: Int
    ) : VkApiResponse

    @Serializable
    public data class ValidatePhoneResponse(
        val type: String? = null,
        val sid: String? = null,
        val delay: Int? = null
    ) : VkApiResponse

    @Serializable
    public data class ProfileInfo(
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
    @Serializable(with = AudioGetCount.Serializer::class)
    public data class AudioGetCount(
        val count : Int,
    ) : VkApiResponse{
        public object Serializer : IntResponseSerializer<AudioGetCount>(
            serialName = "AudioGetCount",
            fromInt = ::AudioGetCount,
            toInt = { it.count }
        )
    }
    @Serializable
    public data class AudioGet(
        val count: Int,
        val items: List<SongResponse> = emptyList()
        //TODO profiles
    ) : VkApiResponse
    @Serializable
    public data class AudioSearch(
        val count: Int,
        val items: List<SongResponse> = emptyList()
    ) : VkApiResponse
    @Serializable(with =  AudioGetById.AudioGetByIdSerializer::class)
    public data class AudioGetById(
        val items: List<SongResponse> = emptyList()
    ) : VkApiResponse {
        public object AudioGetByIdSerializer : KSerializer<AudioGetById> {
            override val descriptor: SerialDescriptor = ListSerializer(SongResponse.serializer()).descriptor

            override fun deserialize(decoder: Decoder): AudioGetById {
                val list =
                    decoder.decodeSerializableValue(ListSerializer(SongResponse.serializer()))
                return AudioGetById(items = list)
            }

            override fun serialize(encoder: Encoder, value: AudioGetById) {
                encoder.encodeSerializableValue(
                    ListSerializer(SongResponse.serializer()),
                    value.items
                )
            }
        }
    }
    @Serializable
    public data class AudioGetPlayLists(
        val count: Int,
        val items: List<AlbumResponse> = emptyList(),
        @SerialName("next_from")
        val nextFrom: String? = null
        //TODO profiles
    ) : VkApiResponse{}

    @Serializable
    public data class AudioSearchPlaylists(
        val count: Int,
        val items: List<AlbumResponse> = emptyList(),

    ) : VkApiResponse

    @Serializable
    public data class AudioSearchAlbums(
        val count: Int,
        val items: List<AlbumResponse>
    ) : VkApiResponse

    @Serializable(with = AddAudioResponse.Serializer::class)
    public data class AddAudioResponse(
        val result: Int
    ) : VkApiResponse {
        public object Serializer : IntResponseSerializer<AddAudioResponse>(
            serialName = "AddAudioResponse",
            fromInt = ::AddAudioResponse,
            toInt = { it.result }
        )
    }

    @Serializable
    public data class AddedAudioResponse(
        @SerialName("audio_id")
        val audioId: Int,
        @SerialName("track_code")
        val trackCode: String? = null
    )

    @Serializable(with = AddAudiosToPlaylistResponse.Serializer::class)
    public data class AddAudiosToPlaylistResponse(
        val result: List<AddedAudioResponse>
    ) : VkApiResponse {
        public object Serializer : ListResponseSerializer<AddAudiosToPlaylistResponse, AddedAudioResponse>(
            itemSerializer = AddedAudioResponse.serializer(),
            fromList = ::AddAudiosToPlaylistResponse,
            toList = { it.result }
        )
    }

    @Serializable(with = DeleteAudioResponse.Serializer::class)
    public data class DeleteAudioResponse(
        val result: Int
    ) : VkApiResponse {
        public object Serializer : IntResponseSerializer<DeleteAudioResponse>(
            serialName = "DeleteAudioResponse",
            fromInt = ::DeleteAudioResponse,
            toInt = { it.result }
        )
    }

    @Serializable(with = RemoveAudiosFromPlaylistResponse.Serializer::class)
    public data class RemoveAudiosFromPlaylistResponse(
        val result: Int
    ) : VkApiResponse {
        public object Serializer : IntResponseSerializer<RemoveAudiosFromPlaylistResponse>(
            serialName = "RemoveAudiosFromPlaylistResponse",
            fromInt = ::RemoveAudiosFromPlaylistResponse,
            toInt = { it.result }
        )
    }

    @Serializable
    public data class CreatePlaylistResponse(
        @SerialName("playlist_id")
        val playlistId: Int
    ) : VkApiResponse

    @Serializable(with = DeletePlaylistResponse.Serializer::class)
    public data class DeletePlaylistResponse(
        val result: Int
    ) : VkApiResponse {
        public object Serializer : IntResponseSerializer<DeletePlaylistResponse>(
            serialName = "DeletePlaylistResponse",
            fromInt = ::DeletePlaylistResponse,
            toInt = { it.result }
        )
    }

}

public inline fun <reified T: VkApiResponse> Json.decodeResponse(jsonString: String): T =
    this.decodeFromJsonElement<T>(
        this.parseToJsonElement(jsonString).jsonObject["response"] ?:
        error("No Response Object")
    )
