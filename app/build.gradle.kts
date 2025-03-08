import org.gradle.internal.os.OperatingSystem
import java.util.Properties

plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.compose)
}

val keystoreProperties = Properties()
keystoreProperties["keyAlias"] = "mykeystore"

val keystorePropertiesFile: File = when {
    OperatingSystem.current().isLinux -> {
        keystoreProperties["debug.keystore"] = "/home/solamour/Dropbox/mystuff/debug.keystore"
        keystoreProperties["storeFile"] = "/home/solamour/Dropbox/mystuff/mykeystore.jks"
        keystoreProperties["serviceCredentialsFile"] = "/home/solamour/Dropbox/mystuff/myapp_firebase_distribution.json"
        rootProject.file("/home/solamour/Dropbox/mystuff/myrelease.keystore")
    }
    OperatingSystem.current().isWindows -> {
        keystoreProperties["debug.keystore"] = "C:\\Users\\solamour\\Dropbox\\mystuff\\debug.keystore"
        keystoreProperties["storeFile"] = "C:\\Users\\solamour\\Dropbox\\mystuff\\mykeystore.jks"
        keystoreProperties["serviceCredentialsFile"] = "C:\\Users\\solamour\\Dropbox\\mystuff\\myapp_firebase_distribution.json"
        rootProject.file("C:\\Users\\solamour\\Dropbox\\mystuff\\myrelease.keystore")
    }
    OperatingSystem.current().isMacOsX -> {
        keystoreProperties["debug.keystore"] = "/Users/a7536987/Downloads/mystuff/debug.keystore"
        keystoreProperties["storeFile"] = "/Users/a7536987/Downloads/mystuff/mykeystore.jks"
        keystoreProperties["serviceCredentialsFile"] = "/Users/a7536987/Downloads/mystuff/myapp_firebase_distribution.json"
        rootProject.file("/Users/a7536987/Downloads/mystuff/myrelease.keystore")
    }
    else -> {
        println("Unknown OS")
        File("")
    }
}
keystoreProperties.load(keystorePropertiesFile.inputStream())

android {
    namespace = "org.solamour.myfoo"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.solamour.myfoo"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        //------------------------------------------------------------------------------------------
    }

    signingConfigs {
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
        getByName("debug") {
        }
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs["release"]

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    //----------------------------------------------------------------------------------------------
}

dependencies {
    // Android
    implementation(libs.androidx.activity.activity.compose)
    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.navigation.compose)

    // Compose
    implementation(platform(libs.androidx.compose.compose.bom))
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.ui.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.ui.tooling)

    // Instrumented Test
    androidTestImplementation(platform(libs.androidx.compose.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.espresso.espresso.core)
    androidTestImplementation(libs.androidx.test.ext.junit.ktx)

    // Unit Test
    testImplementation(libs.junit.junit)

    //----------------------------------------------------------------------------------------------
    implementation(libs.com.google.android.material.material)

    implementation(libs.com.thedeanda.lorem)
}
