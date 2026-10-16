package dev.lcdii.kmp_vk_music_api.di

import dev.lcdii.kmp_vk_music_api.vk_api.HttpClientFactory
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
}