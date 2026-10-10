package dev.lcdii.kmp_vk_music_api.vk_api

class VkApiRequestBuilder {
    private fun buildCustomVkApiRequest(
        method: HttpMethod,
        urlContinuation: String,
        params: Params
    ): VkApiRequest = VkApiRequest(
        method = method,
        params = params
    ).modifyUrl(
        urlContinuation = urlContinuation
    )
}
