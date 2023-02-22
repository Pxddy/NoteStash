package extensions

import common.Version
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureJvmToolchain() {
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(Version.Java.languageVersion)
        }
    }

    extensions.configure<KotlinAndroidProjectExtension> {
        jvmToolchain {
            languageVersion.set(Version.Java.languageVersion)
        }
    }
}