package dev.lcdii.kmp_vk_music_api.vk_api

object IosVkValidationResolver : VkValidationResolver {
    override suspend fun resolve(error: VkApiError.TextError): String {
        println("VK validation type: ${error.validationType.orEmpty()}")
        println(error.description)
        print("Enter VK validation code: ")

        return readlnOrNull()?.trim()?.takeIf { it.isNotEmpty() }
            ?: error("VK validation code was not provided")
    }
}
