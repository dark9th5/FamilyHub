plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

import java.util.Properties

fun Project.readLocalProperty(name: String, defaultValue: String): String {
    val localPropsFile = rootProject.file("local.properties")
    if (!localPropsFile.exists()) return defaultValue
    val properties = Properties().apply {
        localPropsFile.inputStream().use { load(it) }
    }
    return properties.getProperty(name)?.takeIf { it.isNotBlank() } ?: defaultValue
}

android {
    namespace = "com.family.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.family.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val ngrokHttpsUrl = readLocalProperty("NGROK_HTTPS_BASE_URL", "https://example.ngrok-free.app/")
        val ngrokWssUrl = readLocalProperty("NGROK_WSS_CHAT_URL", "wss://example.ngrok-free.app/ws-chat-raw")
        buildConfigField("String", "NGROK_HTTPS_BASE_URL", "\"$ngrokHttpsUrl\"")
        buildConfigField("String", "NGROK_WSS_CHAT_URL", "\"$ngrokWssUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")

    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.navigation:navigation-compose:2.8.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("io.coil-kt:coil-compose:2.7.0")

    debugImplementation(composeBom)
    debugImplementation("androidx.compose.ui:ui-tooling")
}
