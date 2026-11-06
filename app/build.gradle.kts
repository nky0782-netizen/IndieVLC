plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.indievlc.player"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.indievlc.player"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
}

dependencies {
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("org.videolan.android:libvlc-all:3.6.0")
}
