import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val javafxVersion = libs.versions.javafx.get()
val javafxBase = libs.openjfx.base.get()
val javafxGraphics = libs.openjfx.graphics.get()
val javafxWeb = libs.openjfx.web.get()
val javafxControls = libs.openjfx.controls.get()

val javafxClassifier = when {
    System.getProperty("os.name").startsWith("Windows") -> "win"
    System.getProperty("os.name").startsWith("Mac") && System.getProperty("os.arch") == "aarch64" -> "mac-aarch64"
    System.getProperty("os.name").startsWith("Mac") -> "mac"
    System.getProperty("os.name").startsWith("Linux") && System.getProperty("os.arch") == "aarch64" -> "linux-aarch64"
    System.getProperty("os.name").startsWith("Linux") -> "linux"
    else -> error("Unsupported JavaFX platform: ${System.getProperty("os.name")} ${System.getProperty("os.arch")}")
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlinx.resources)
}

kotlin {
    explicitApi()
    android {
        namespace = "dev.lcdii.kmp_vk_music_api"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    iosArm64()
    iosSimulatorArm64()
    iosX64()

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.resources)
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            api(libs.koin.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            implementation(libs.ksafe)

            implementation(libs.vk.captcha)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation("${javafxBase.group}:${javafxBase.name}:$javafxVersion:$javafxClassifier")
            implementation("${javafxGraphics.group}:${javafxGraphics.name}:$javafxVersion:$javafxClassifier")
            implementation("${javafxWeb.group}:${javafxWeb.name}:$javafxVersion:$javafxClassifier")
            implementation("${javafxControls.group}:${javafxControls.name}:$javafxVersion:$javafxClassifier")
            implementation("org.openjfx:javafx-media:21:${javafxClassifier}")
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }

}
