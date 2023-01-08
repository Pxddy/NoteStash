plugins {
    `kotlin-dsl`
}

group = "com.ph.notestash.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
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