package dev.lcdii.kmp_vk_music_api.di

import org.koin.core.module.Module

public object VkApiKoinModules {
    public val modules: List<Module> = listOf(vkApiModule, platformVkApiModule)
}