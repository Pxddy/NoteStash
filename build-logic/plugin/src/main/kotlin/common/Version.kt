package common

import org.gradle.api.JavaVersion
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object Version {

    object Release {
        private const val major = 1
        private const val minor = 0
        private const val patch = 0

        const val versionCode = (major * 10000) + (minor * 100) + (patch * 1)
        const val versionName = "$major.$minor$patch"
    }

    object Sdk {
        const val min = 21
        const val target = 34
        const val compile = 34
    }

    object Java {
        val version = JavaVersion.VERSION_11
        val jvmTarget = JvmTarget.JVM_11
        val languageVersion: JavaLanguageVersion = JavaLanguageVersion.of(version.majorVersion)
    }
}