package com.ph.notestash.testutils

import org.junit.rules.TestWatcher
import org.junit.runner.Description
import timber.log.Timber

class TimberRule : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Timber.plant(Timber.DebugTree())
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Timber.uprootAll()
    }
}