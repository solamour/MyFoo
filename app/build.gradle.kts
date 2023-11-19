plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
}

import java.io.FileInputStream
import java.util.Properties
import org.gradle.internal.os.OperatingSystem

// https://developer.android.com/studio/publish/app-signing#secure-shared-keystore
val keystoreProperties = Properties()
keystoreProperties["keyAlias"] = "mykeystore"

var keystorePropertiesFile: File
if (OperatingSystem.current().isLinux()) {
    keystorePropertiesFile = rootProject.file("/home/solamour/Dropbox/mystuff/myrelease.keystore")
    keystoreProperties["storeFile"] = "/home/solamour/Dropbox/mystuff/mykeystore.jks"
    keystoreProperties["serviceCredentialsFile"] = "/home/solamour/Dropbox/mystuff/myapp_firebase_distribution.json"
    keystoreProperties["debug.keystore"] = "/home/solamour/Dropbox/mystuff/debug.keystore"
    //keystorePropertiesFile = rootProject.file("/mnt/c/Users/solamour/Dropbox/mystuff/myrelease.keystore")
    //keystoreProperties["storeFile"] = "/mnt/c/Users/solamour/Dropbox/mystuff/mykeystore.jks"
    //keystoreProperties["serviceCredentialsFile"] = "/mnt/c/Users/solamour/Dropbox/mystuff/myapp_firebase_distribution.json"
    //keystoreProperties["debug.keystore"] = "/mnt/c/Users/solamour/Dropbox/mystuff/debug.keystore"
} else if (OperatingSystem.current().isWindows()) {
    keystorePropertiesFile = rootProject.file("C:\\Users\\solamour\\Dropbox\\mystuff\\myrelease.keystore")
    keystoreProperties["storeFile"] = "C:\\Users\\solamour\\Dropbox\\mystuff\\mykeystore.jks"
    keystoreProperties["serviceCredentialsFile"] = "C:\\Users\\solamour\\Dropbox\\mystuff\\myapp_firebase_distribution.json"
    keystoreProperties["debug.keystore"] = "C:\\Users\\solamour\\Dropbox\\mystuff\\debug.keystore"
} else if (OperatingSystem.current().isMacOsX()) {
    keystorePropertiesFile = rootProject.file("/Users/a7536987/Downloads/mystuff/myrelease.keystore")
    keystoreProperties["storeFile"] = "/Users/a7536987/Downloads/mystuff/mykeystore.jks"
    keystoreProperties["serviceCredentialsFile"] = "/Users/a7536987/Downloads/mystuff/myapp_firebase_distribution.json"
    keystoreProperties["debug.keystore"] = "/Users/a7536987/Downloads/mystuff/debug.keystore"
} else {
    println("Unknown OS")
    keystorePropertiesFile = File("")
}
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "org.solamour.myfoo"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.solamour.myfoo"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.x"   // Release tag will be "1.0.x".

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // https://developer.android.com/studio/publish/app-signing.html
    signingConfigs {
        // If defined, it overrides Android SDK's default "debug.keystore".
        getByName("debug") {
            storeFile = file(keystoreProperties["debug.keystore"] as String)
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs["release"]
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_9
        targetCompatibility = JavaVersion.VERSION_1_9
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_9.toString()
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        // https://developer.android.com/jetpack/androidx/releases/compose-kotlin
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.core:core-ktx:1.12.0")         // "targetSdk = 32" -> "1.7.0".
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // https://developer.android.com/jetpack/compose/bom/bom-mapping
    val composeBom = platform("androidx.compose:compose-bom:2023.09.01")
    implementation(composeBom)
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("com.google.android.material:material:1.10.0")

    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    implementation("com.thedeanda:lorem:2.1")
}
