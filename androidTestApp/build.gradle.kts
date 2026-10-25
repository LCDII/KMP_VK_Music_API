plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "dev.lcdii.kmp_vk_music_api.testapp"
    compileSdk = 37

    defaultConfig {
        applicationId = "dev.lcdii.kmp_vk_music_api.testapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":library"))
    implementation(libs.koin.android)
    implementation(libs.kotlinx.coroutines.android)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}
