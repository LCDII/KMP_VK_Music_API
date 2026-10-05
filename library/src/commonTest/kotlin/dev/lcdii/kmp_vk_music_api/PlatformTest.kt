package dev.lcdii.kmp_vk_music_api

import kotlin.test.Test
import kotlin.test.assertTrue

class PlatformTest {
    @Test
    fun platformNameIsAvailable() {
        assertTrue(platformName().isNotBlank())
    }
}
