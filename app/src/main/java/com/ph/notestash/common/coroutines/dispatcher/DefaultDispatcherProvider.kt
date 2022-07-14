package com.ph.notestash.common.coroutines.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    override val Default: CoroutineDispatcher
        get() = Dispatchers.Default

    override val Main: CoroutineDispatcher
        get() = Dispatchers.Main

    override val MainImmediate: CoroutineDispatcher
        get() = Dispatchers.Main.immediate

    override val IO: CoroutineDispatcher
        get() = Dispatchers.IO

    override val Unconfined: CoroutineDispatcher
        get() = Dispatchers.Unconfined
}