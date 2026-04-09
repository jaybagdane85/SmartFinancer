plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Firebase Plugin
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.smartfinancer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.smartfinancer"
        minSdk = 24
        targetSdk = 35
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase BOM for consistent versions
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Firebase Firestore (for database)
    implementation("com.google.firebase:firebase-firestore")
}
