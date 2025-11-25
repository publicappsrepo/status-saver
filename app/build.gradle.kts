plugins {
    alias(libs.plugins.agp)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.androidx.safeargs)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.appsease.status.saver"

    signingConfigs {
        create("config") {
            storeFile = file("/Users/akshayvadchhakgmail.com/Desktop/Safe Project/jks/status_saver.jks")
            storePassword = "android"
            keyAlias = "android"
            keyPassword = "android"
        }
    }

    compileSdk = 36

    defaultConfig {
        applicationId = "com.appsease.status.saver"
        minSdk = 24
        targetSdk = 36
//        versionCode = 1
//        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isZipAlignEnabled = true
            isShrinkResources = false
            versionNameSuffix = ".debug"

            // Disable automatic build ID generation
            ext["alwaysUpdateBuildId"] = false

            // Disable PNG crunching
            isCrunchPngs = false

            externalNativeBuild {
                cmake {
                    cppFlags += "-DDEBUG"
                    abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
                }
            }
        }

        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isZipAlignEnabled = true
            isShrinkResources = true

            externalNativeBuild {
                cmake {
                    cppFlags += "-DRELEASE"
                    abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
                }
            }
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
        buildConfig = true
        viewBinding = true
    }

    flavorDimensions += "default"

    productFlavors {
        create("status_saver") {
            applicationId = "com.appsease.status.saver"
            manifestPlaceholders["app_content_provider"] = "com.appsease.status.saver"
            versionCode = 5
            versionName = "5.0"
            dimension = "default"
            signingConfig = signingConfigs.getByName("config")

            // Equivalent of Groovy's setProperty("archivesBaseName", ...)
            setProperty("archivesBaseName", "$versionName.$versionCode")
        }
    }
}



dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.common.java8)

    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.bundles.media3)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.loader)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.material.components)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.coil)
    implementation(libs.coil.video)

    implementation(libs.photoview)
    implementation(libs.bundles.ktor)
    implementation(libs.versioncompare)
    implementation(libs.libphonenumber)
    implementation(libs.prettytime)
    implementation(libs.advrecyclerview)

    implementation(libs.markdown.core)
    implementation(libs.markdown.html)

    // Kotlin
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-analytics")
}