import org.gradle.internal.os.OperatingSystem
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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
        debug {
        }

        release {
            isMinifyEnabled = true
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
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    //----------------------------------------------------------------------------------------------
}

dependencies {
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.junit.ktx)

    debugImplementation(libs.compose.ui.test.manifest)
    debugImplementation(libs.compose.ui.tooling)

    //----------------------------------------------------------------------------------------------
    implementation(libs.lorem)
    implementation(libs.material)
}
