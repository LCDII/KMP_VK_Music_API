package dev.lcdii.kmp_vk_music_api.di

import dev.lcdii.kmp_vk_music_api.vk_api.domain.ApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.domain.AppClient
import dev.lcdii.kmp_vk_music_api.vk_api.domain.TokenStorage
import dev.lcdii.kmp_vk_music_api.vk_api.data.KSafeTokenStorage
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.data.VkMobileAppClient
import dev.lcdii.kmp_vk_music_api.vk_api.data.HttpClientFactory
import dev.lcdii.kmp_vk_music_api.vk_api.data.VkApiJson
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val platformVkApiModule: Module


internal val vkApiModule: Module = module {
    includes(platformVkApiModule)

    single<Json> { VkApiJson }
    single<HttpClient> { HttpClientFactory.create(get()) }
    single<AppClient> { VkMobileAppClient }
    single<ApiSource> { VkApiSource(get(), get(), get(), get()) }
    single<VkApiSource> { VkApiSource(get(), get(), get(), get()) }
    single<TokenStorage> { KSafeTokenStorage(get()) }
}

