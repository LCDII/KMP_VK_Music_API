plugins{
    alias(libs.plugins.androidApplication)
}


android{
    namespace = "dev.lcdii.kmp_vk_music_api.test"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig{
        applicationId = "dev.lcdii.kmp_vk_music_api.test"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.compileSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":vk-music-api"))
    implementation(libs.koin.android)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
}
