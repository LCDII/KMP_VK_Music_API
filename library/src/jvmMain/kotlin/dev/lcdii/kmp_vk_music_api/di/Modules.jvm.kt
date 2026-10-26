package dev.lcdii.kmp_vk_music_api.di

import dev.lcdii.kmp_vk_music_api.vk_api.JvmAuth
import eu.anifantakis.lib.ksafe.KSafe
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { OkHttp.create() }
        single<KSafe> { KSafe() }
        factory<JvmAuth> { JvmAuth(get()) }
    }
