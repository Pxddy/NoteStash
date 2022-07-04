package com.ph.notestash.common.coroutines.dispatcher

import kotlin.coroutines.CoroutineContext

@Suppress("PropertyName")
interface DispatcherProvider {
    val Default: CoroutineContext
    val Main: CoroutineContext
    val MainImmediate: CoroutineContext
    val IO: CoroutineContext
    val Unconfined: CoroutineContext
}