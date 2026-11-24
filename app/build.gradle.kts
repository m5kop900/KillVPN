import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Release signing: read from env vars (CI) or from keystore.properties (local, git-ignored).
val keystoreProps = Properties().apply {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

fun signingValue(env: String, prop: String): String? =
    System.getenv(env)?.takeIf { it.isNotBlank() } ?: keystoreProps.getProperty(prop)

val releaseStorePath: String? = signingValue("KEYSTORE_PATH", "storeFile")
val hasReleaseKey: Boolean = releaseStorePath != null && rootProject.file(releaseStorePath).exists()

if (!hasReleaseKey && gradle.startParameter.taskNames.any { it.contains("Release", ignoreCase = true) }) {
    logger.warn("WARNING: no release keystore configured, the release APK will be signed with the DEBUG key.")
}

android {
    namespace = "com.mkop.killvpn"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mkop.killvpn"
        minSdk = 24            // Android 7.0
        targetSdk = 34
        versionCode = 2
        versionName = "1.2.0"
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        if (hasReleaseKey) {
            create("release") {
                storeFile = rootProject.file(releaseStorePath!!)
                storePassword = signingValue("KEYSTORE_PASSWORD", "storePassword")
                keyAlias = signingValue("KEY_ALIAS", "keyAlias")
                keyPassword = signingValue("KEY_PASSWORD", "keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = if (hasReleaseKey) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }

    // Don't embed the dependency-metadata block in the APK (smaller, reproducible).
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions { jvmTarget = "1.8" }
}
