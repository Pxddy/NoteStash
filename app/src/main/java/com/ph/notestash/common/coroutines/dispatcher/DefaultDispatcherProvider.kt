package com.ph.notestash.common.coroutines.dispatcher

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    override val Default: CoroutineContext
        get() = Dispatchers.Default

    override val Main: CoroutineContext
        get() = Dispatchers.Main

    override val MainImmediate: CoroutineContext
        get() = Dispatchers.Main.immediate

    override val IO: CoroutineContext
        get() = Dispatchers.IO

    override val Unconfined: CoroutineContext
        get() = Dispatchers.Unconfined
}