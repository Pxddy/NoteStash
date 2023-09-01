plugins {
    id("notestash.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.ph.core.testing"
}

dependencies {
    implementation(project(":app"))

    debugApi(libs.androidx.fragment.testing)
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.dataStore.core)

    implementation(libs.bundles.androidx.room)

    implementation(libs.androidx.test.runner)

    implementation(libs.hilt.testing)
    ksp(libs.hilt.compiler)

    api(libs.kotlinx.coroutines.test)
    api(libs.androidx.test.core.ktx)
    api(libs.kotest.assertions)
}