plugins {
    alias(libs.plugins.kotlin.compose)
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.owais.milktracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.owais.milktracker"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.1.2.beta"

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
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
    buildFeatures {
        compose = true
    }
}

configurations.all {
    exclude(group = "com.google.guava", module = "listenablefuture")
}


dependencies {

    // ───────── Compose (BOM-driven) ─────────
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // ───────── Lifecycle / Navigation ─────────
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ───────── Room ─────────
    val room_version = "2.7.0-beta01"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // ───────── DataStore ─────────
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // ───────── WorkManager ─────────
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // ───────── Material (XML themes) ─────────
    implementation("com.google.android.material:material:1.11.0")

    // ───────── Guava (FIXED) ─────────
    implementation("com.google.guava:guava:23.0") {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }

    // ───────── Testing ─────────
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}
