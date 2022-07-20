plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 21
        targetSdk = 32
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

    dependencies {
        implementation(project(":app"))

        implementation(libs.bundles.androidx.test)
        implementation(libs.kotlinx.coroutines.test)

        implementation(libs.androidx.dataStore.core)

        implementation(libs.bundles.androidx.room)

        implementation(libs.hilt.testing)
        kapt(libs.hilt.compiler)

        testImplementation(libs.bundles.jupiter)
        testImplementation(libs.kotest.assertions)
    }
}