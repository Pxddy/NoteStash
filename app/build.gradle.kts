import common.Version

plugins {
    id("notestash.android.application")
    alias(libs.plugins.androidx.navigation.safeargs.gradlePlugin)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.hilt.gradlePlugin)
    alias(libs.plugins.ksp)
}

android {

    namespace = "com.ph.notestash"

    defaultConfig {
        applicationId = "com.ph.notestash"
        versionCode = Version.Release.versionCode
        versionName = Version.Release.versionName

        testInstrumentationRunner = "com.ph.core.testing.NoteStashTestRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    testOptions {
        animationsDisabled = true

        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true

            all {
                it.useJUnitPlatform()
            }
        }
    }
}

hilt {
    enableAggregatingTask = true
}

ksp {
    arg("room.generateKotlin", "true")
}

room {
    schemaDirectory("$projectDir/schemas/")
}

dependencies {

    testImplementation(project(":core-testing"))
    androidTestImplementation(project(":core-testing"))

    androidTestImplementation(libs.bundles.androidx.test.espresso)
    androidTestImplementation(libs.androidx.test.ext)

    androidTestImplementation(libs.hilt.testing)
    kspAndroidTest(libs.hilt.compiler)

    testImplementation(libs.bundles.jupiter)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    implementation(libs.androidx.appcompat)

    // Material
    implementation(libs.material3)

    // Layouts
    implementation(libs.bundles.androidx.layout)

    // Activity
    implementation(libs.androidx.activity.ktx)

    // Fragment
    implementation(libs.androidx.fragment.ktx)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Navigation
    implementation(libs.bundles.androidx.navigation)

    // Timber
    implementation(libs.timber)

    // Coroutines
    implementation(libs.bundles.kotlinx.coroutines)

    // Room
    implementation(libs.bundles.androidx.room)
    ksp(libs.androidx.room.compiler)

    // Paging
    implementation(libs.androidx.paging.runtime)

    // Recyclerview
    implementation(libs.androidx.recyclerview)

    // DataStore
    implementation(libs.androidx.dataStore.core)

    // Moshi
    implementation(libs.moshi.moshi)
    ksp(libs.moshi.codegen)

    implementation(libs.simpleViewBinding)
}