package dev.lcdii.kmp_vk_music_api.di

import dev.lcdii.kmp_vk_music_api.vk_api.ApiService
import dev.lcdii.kmp_vk_music_api.vk_api.ApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.AppClient
import dev.lcdii.kmp_vk_music_api.vk_api.AudioRepository
import dev.lcdii.kmp_vk_music_api.vk_api.AuthRepository
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiService
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.VkAudioRepository
import dev.lcdii.kmp_vk_music_api.vk_api.VkAuthRepository
import dev.lcdii.kmp_vk_music_api.vk_api.VkMobileAppClient
import dev.lcdii.kmp_vk_music_api.vk_api.data.HttpClientFactory
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module


val vkApiModule: Module = module {
    includes(platformModule)

    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
    single { HttpClientFactory.create(get()) }
    single<AppClient> { VkMobileAppClient }
    singleOf(::VkApiSource).bind<ApiSource>()
    singleOf(::VkAudioRepository).bind<AudioRepository>()
    singleOf(::VkAuthRepository).bind<AuthRepository>()
    singleOf(::VkApiService).bind<ApiService>()
}
