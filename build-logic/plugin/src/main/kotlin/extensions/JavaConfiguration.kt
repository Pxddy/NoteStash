package extensions

import common.Version
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure

internal fun Project.configureJava() {
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(Version.Java.languageVersion)
        }
    }
}