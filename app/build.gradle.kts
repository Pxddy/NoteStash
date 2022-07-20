plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.androidx.navigation.safeargs)
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.ph.notestash"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "com.ph.core.NoteStashTestRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
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
        jvmTarget = JavaVersion.VERSION_11.toString()

        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlin.time.ExperimentalTime",
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlin.ExperimentalStdlibApi"
        )
    }

    hilt {
        enableAggregatingTask = true
    }

    kapt {
        // Recommended for Hilt
        correctErrorTypes = true
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", true)
            arg("room.expandProjection", true)
        }
    }

    buildFeatures {
        viewBinding = true
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

dependencies {

    testImplementation(project(":core-testing"))
    androidTestImplementation(project(":core-testing"))

    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotest.assertions)
    androidTestImplementation(libs.hilt.testing)
    kaptAndroidTest(libs.hilt.compiler)

    testImplementation(libs.bundles.jupiter)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    implementation(libs.androidx.core.ktx)
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
    kapt(libs.hilt.compiler)

    // Navigation
    implementation(libs.bundles.androidx.navigation)

    // Timber
    implementation(libs.timber)

    // Coroutines
    implementation(libs.bundles.kotlinx.coroutines)

    // Room
    implementation(libs.bundles.androidx.room)
    kapt(libs.androidx.room.compiler)

    // Paging
    implementation(libs.androidx.paging.runtime)

    // Recyclerview
    implementation(libs.androidx.recyclerview)

    // DataStore
    implementation(libs.androidx.dataStore.core)

    // Moshi
    implementation(libs.moshi.moshi)
    kapt(libs.moshi.codegen)
}