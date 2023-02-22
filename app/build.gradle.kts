import common.Version

@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    id("notestash.android.application")
    alias(libs.plugins.androidx.navigation.safeargs.gradlePlugin)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.gradlePlugin)
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

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += arrayOf(
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )

                compilerArgumentProviders(
                    RoomSchemaArgProvider(schemaDir = File(projectDir, "schemas"))
                )
            }
        }
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

hilt {
    enableAggregatingTask = true
}

kapt {
    // Recommended for Hilt
    correctErrorTypes = true
}

dependencies {

    testImplementation(project(":core-testing"))
    androidTestImplementation(project(":core-testing"))

    androidTestImplementation(libs.bundles.androidx.test.espresso)
    androidTestImplementation(libs.androidx.test.ext)

    androidTestImplementation(libs.hilt.testing)
    kaptAndroidTest(libs.hilt.compiler)

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

    implementation(libs.simpleViewBinding)
}

private class RoomSchemaArgProvider(
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File
) : CommandLineArgumentProvider {

    override fun asArguments(): Iterable<String> {
        // Note: If you're using KSP, you should change the line below to return
        // listOf("room.schemaLocation=${schemaDir.path}")
        return listOf("-Aroom.schemaLocation=${schemaDir.path}")
    }
}
