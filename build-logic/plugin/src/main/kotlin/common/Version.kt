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
        const val target = 35
        const val compile = 35
    }

    object Java {
        val version = JavaVersion.VERSION_21
        val jvmTarget = JvmTarget.JVM_21
        val languageVersion: JavaLanguageVersion = JavaLanguageVersion.of(version.majorVersion)
    }
}
