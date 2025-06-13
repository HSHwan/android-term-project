import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
}



val localProps = Properties().apply {
    val propFile = rootProject.file("local.properties")
    if (propFile.exists()) {
        load(propFile.inputStream())
    }
}

android {
    namespace = "com.eatdel.eattoplan"
    compileSdk = 35

    val mapsKey: String = localProps.getProperty("GOOGLE_MAPS_API_KEY", "")
    android.buildFeatures.buildConfig = true



    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.eatdel.eattoplan"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // 3A) BuildConfig 필드로 주입
        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"$mapsKey\"")

        // 3B) strings.xml 에도 주입하고 싶다면
        resValue("string", "google_maps_key", mapsKey)

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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.auth)
    implementation(libs.datastore.preferences)
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // Places SDK for Android
    implementation("com.google.android.libraries.places:places:2.6.0")
    // 위치 획득용 Play Services
    implementation("com.google.android.gms:play-services-location:21.0.1")
}