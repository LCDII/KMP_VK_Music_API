package dev.lcdii.kmp_vk_music_api.di


import dev.lcdii.kmp_vk_music_api.vk_api.AndroidAuth
import eu.anifantakis.lib.ksafe.KSafe
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformVkApiModule: Module
    get() = module {
        single<HttpClientEngine> { OkHttp.create() }
        single<KSafe> { KSafe(androidContext()) }
        factory<AndroidAuth> { AndroidAuth(get()) }
    }