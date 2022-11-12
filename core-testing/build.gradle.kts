plugins {
    id("notestash.android.library")
    kotlin("kapt")
}

android{
    namespace = "com.ph.core.testing"
}

dependencies {
    implementation(project(":app"))

    debugImplementation(libs.androidx.fragment.testing)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines.test)

    implementation(libs.androidx.dataStore.core)

    implementation(libs.bundles.androidx.room)

    implementation(libs.androidx.test.runner)

    implementation(libs.hilt.testing)
    kapt(libs.hilt.compiler)
}