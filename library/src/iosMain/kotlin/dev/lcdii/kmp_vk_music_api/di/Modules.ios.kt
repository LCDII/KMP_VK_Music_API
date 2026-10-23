package dev.lcdii.kmp_vk_music_api.di

import dev.lcdii.kmp_vk_music_api.vk_api.IosTokenStorage
import dev.lcdii.kmp_vk_music_api.vk_api.IosVkCaptchaResolver
import dev.lcdii.kmp_vk_music_api.vk_api.TokenStorage
import eu.anifantakis.lib.ksafe.KSafe
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { Darwin.create() }
        //single<TokenStorage> { IosTokenStorage() }
        single<KSafe> { KSafe() }
    }