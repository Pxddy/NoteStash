plugins {
    `kotlin-dsl`
}

group = "com.ph.notestash.buildlogic"

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "notestash.android.application"
            implementationClass = "AndroidApplicationPlugin"
        }
        register("androidLibrary") {
            id = "notestash.android.library"
            implementationClass = "AndroidLibraryPlugin"
        }
    }
}