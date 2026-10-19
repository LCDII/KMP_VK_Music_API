package dev.lcdii.kmp_vk_music_api.di

import dev.lcdii.kmp_vk_music_api.vk_api.ApiService
import dev.lcdii.kmp_vk_music_api.vk_api.ApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.AppClient
import dev.lcdii.kmp_vk_music_api.vk_api.AudioRepository
import dev.lcdii.kmp_vk_music_api.vk_api.AuthRepository
import dev.lcdii.kmp_vk_music_api.vk_api.HttpClientFactory
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiService
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.VkAudioRepository
import dev.lcdii.kmp_vk_music_api.vk_api.VkAuthRepository
import dev.lcdii.kmp_vk_music_api.vk_api.VkMobileAppClient
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module

expect val platformModule: Module

internal val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    single<AppClient>{ VkMobileAppClient }
    singleOf(::VkApiSource).bind<ApiSource>()
    singleOf(::VkAudioRepository).bind<AudioRepository>()
    singleOf(::VkAuthRepository).bind<AuthRepository>()
    singleOf(::VkApiService).bind<ApiService>()
}

internal object LibraryKoinContext {
    private var koinApplication: KoinApplication? = null

    fun init(appDeclaration: KoinAppDeclaration? = null) {
        if (koinApplication == null) {
            koinApplication = koinApplication {
                appDeclaration?.invoke(this)
                modules(sharedModule + platformModule)
            }
        }
    }

}

object VkApiLibrary {
    fun init(appDeclaration: KoinAppDeclaration? = null) {
        LibraryKoinContext.init(appDeclaration)
    }
}