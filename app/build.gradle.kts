plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.bookswap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bookswap"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.2"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            // Подписывается debug-ключом, чтобы APK ставился на устройство без настройки keystore.
            // Для публикации нужно завести собственный ключ.
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")

    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
