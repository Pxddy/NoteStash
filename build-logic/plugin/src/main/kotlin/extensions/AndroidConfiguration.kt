package extensions

import com.android.build.api.dsl.CommonExtension
import common.Version
import common.libs
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = Version.Sdk.compile

        defaultConfig {
            minSdk = Version.Sdk.min
        }

        compileOptions {
            // Flag to enable support for the new language APIs
            isCoreLibraryDesugaringEnabled = true

            sourceCompatibility = Version.Java.version
            targetCompatibility = Version.Java.version

        }
    }

    configureKotlinJvm()

    dependencies {
        add("coreLibraryDesugaring", libs.android.desugarJdkLibs)
    }
}

private fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = Version.Java.version
        targetCompatibility = Version.Java.version
    }

    configureKotlin()
}

private fun Project.configureKotlin() = configure<KotlinAndroidProjectExtension> {
    compilerOptions {
        jvmTarget.set(Version.Java.jvmTarget)

        freeCompilerArgs.addAll(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlin.time.ExperimentalTime",
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlin.ExperimentalStdlibApi"
        )
    }
}