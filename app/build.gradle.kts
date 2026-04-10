import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.apptivelabs.mypocketdev"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.apptivelabs.mypocketdev"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        val localPropsFile = rootProject.file("local.properties")
        val claudeApiKey = if (localPropsFile.exists()) {
            val props = Properties()
            props.load(localPropsFile.inputStream())
            props.getProperty("CLAUDE_API_KEY", "")
        } else ""

        buildConfigField("String", "CLAUDE_API_KEY", "\"$claudeApiKey\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // composeOptions not needed — managed by org.jetbrains.kotlin.plugin.compose

    kotlinOptions {
        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.11.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Activity & Lifecycle
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Ktor (HTTP client for Claude API)
    val ktorVersion = "2.3.12"
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
}
