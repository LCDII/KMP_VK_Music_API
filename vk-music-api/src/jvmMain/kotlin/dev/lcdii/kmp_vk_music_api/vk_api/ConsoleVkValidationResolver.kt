package dev.lcdii.kmp_vk_music_api.vk_api

import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.core.VkApiError
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkValidationResolver

internal object ConsoleVkValidationResolver : VkValidationResolver {
    override suspend fun resolve(
        error: VkApiError.Auth.Validation
    ): Result<String, DataError> {
        println("VK validation type: ${error.validationType.orEmpty()}")
        println(error.description)
        print("Enter VK validation code: ")

        val code = readlnOrNull()?.trim()

        return if (code.isNullOrEmpty()) {
            Result.Error(DataError.Remote.Unknown)
        } else {
            Result.Success(code)
        }
    }
}
