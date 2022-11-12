package com.ph.core.testing.common.coroutines.dispatcher

import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class TestDispatcherProvider(
    scheduler: TestCoroutineScheduler? = null
) : DispatcherProvider {

    private val testDispatcher = UnconfinedTestDispatcher(
        scheduler = scheduler,
        name = "UnconfinedTestDispatcher"
    )

    override val Default: CoroutineDispatcher
        get() = testDispatcher

    override val Main: CoroutineDispatcher
        get() = testDispatcher

    override val MainImmediate: CoroutineDispatcher
        get() = testDispatcher

    override val IO: CoroutineDispatcher
        get() = testDispatcher

    override val Unconfined: CoroutineDispatcher
        get() = testDispatcher
}