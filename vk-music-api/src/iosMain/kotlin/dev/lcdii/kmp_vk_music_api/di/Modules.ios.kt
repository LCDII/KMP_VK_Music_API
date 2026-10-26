package dev.lcdii.kmp_vk_music_api.di

import dev.lcdii.kmp_vk_music_api.vk_api.IosAuth
import eu.anifantakis.lib.ksafe.KSafe
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformVkApiModule: Module
    get() = module {
        single<HttpClientEngine> { Darwin.create() }
        single<KSafe> { KSafe() }
        factory<IosAuth> { IosAuth(get()) }
    }