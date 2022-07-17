package com.ph.notestash.testutils

import android.util.Log
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class TimberExtension : BeforeAllCallback, AfterAllCallback {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    private val printTree = object : Timber.DebugTree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            val formattedDateTime = LocalDateTime.now().format(dateTimeFormatter)
            println("$formattedDateTime ${priority.toPriorityString}/$tag: $message")
        }
    }

    override fun beforeAll(context: ExtensionContext?) {
        Timber.plant(printTree)
    }

    override fun afterAll(context: ExtensionContext?) {
        Timber.uproot(printTree)
    }

    private val Int.toPriorityString: String
        get() = when (this) {
            Log.ERROR -> "E"
            Log.WARN -> "W"
            Log.INFO -> "I"
            Log.DEBUG -> "D"
            Log.VERBOSE -> "V"
            else -> toString()
        }
}