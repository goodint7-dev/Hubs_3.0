plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ab.hubs_30"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ab.hubs_30"
        minSdk = 24
        targetSdk = 35
        versionCode = 4
        versionName = "1.3.rc1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}
dependencies {
    // 1. Core Android Basics (Stable Downgrades)    // Downgrading from 1.17.0 -> 1.13.1
    implementation("androidx.core:core-ktx:1.13.1")
    // Downgrading from 2.10.0 -> 2.8.6
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    // Downgrading from 1.12.1 -> 1.9.3
    implementation("androidx.activity:activity-compose:1.9.3")

    // 2. The Compose Bill of Materials (BOM)
    // This controls the versions of ui, material3, and graphics.
    // Downgrading from 2025.12.00 -> 2024.10.00 (This is the critical fix for IDE crashes)
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))

    // These libraries now automatically use the stable versions defined by the BOM above
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // 3. Navigation
    // Downgrading from 2.9.6 -> 2.8.3 (Rock-solid stability)
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // 4. ViewModel for Compose
    // Downgrading from 2.10.0 -> 2.8.6
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // Optional: If you use testing, make sure these match the BOM too
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

}