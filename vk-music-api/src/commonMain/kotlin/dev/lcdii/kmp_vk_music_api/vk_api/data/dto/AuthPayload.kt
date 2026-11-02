package dev.lcdii.kmp_vk_music_api.vk_api.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class AuthPayload(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("user_id")
    val userId: Int
)
