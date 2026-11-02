plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":vk-music-api"))
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
