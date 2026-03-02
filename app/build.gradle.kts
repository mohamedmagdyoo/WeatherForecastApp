plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)               // Note: ksp version must match kotlin version in libs.versions.toml
    kotlin("plugin.serialization") version "2.0.21"
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.labs"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.labs"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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

    // Note: jvmToolchain(17) in kotlin{} block below handles this automatically
    // but keep compileOptions in sync to avoid JVM mismatch errors with KSP
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

// Note: jvmToolchain sets Java + Kotlin + KSP all to same JVM version in one place
// replaces the need for kotlinOptions { jvmTarget = "17" } separately
kotlin {
    jvmToolchain(17)
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Lifecycle + lifecycleScope support
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Compose BOM --> manages all compose library versions automatically
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Google Play Services
    implementation(libs.play.services.location)

    // Coil --> image loading for Compose
    implementation(libs.coil.compose)

    // Gson --> standalone JSON serialization
    // Note: converter-gson already bundles gson internally, but explicit dependency gives version control
    implementation(libs.gson)

    // Retrofit --> 2 dependencies
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // WorkManager --> background task scheduling
    implementation(libs.work.runtime.ktx)

    // Room --> 3 dependencies
    // Note: room-compiler uses ksp() not implementation() — required for code generation
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    //Navigation
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

}