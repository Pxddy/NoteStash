// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.kapt).apply(false)
    alias(libs.plugins.hilt).apply(false)
    alias(libs.plugins.androidx.navigation.safeargs).apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}