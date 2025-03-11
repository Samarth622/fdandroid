plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.foodlens"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.foodlens"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

// integrate backend
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)

    implementation(libs.coil.compose)

    implementation(libs.logging.interceptor)

    implementation("com.github.yalantis:ucrop:2.2.8")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation(libs.androidx.compose.bom.v20240200)
    implementation(libs.androidx.webkit)
// Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.browser)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.navigation.compose.v275)
    implementation (libs.androidx.core.ktx.v170)

    implementation (libs.androidx.datastore.preferences)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}