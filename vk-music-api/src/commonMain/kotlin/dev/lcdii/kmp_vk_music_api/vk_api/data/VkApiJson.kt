package dev.lcdii.kmp_vk_music_api.vk_api.data

import kotlinx.serialization.json.Json

public val VkApiJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}