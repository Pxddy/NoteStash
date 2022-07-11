package com.ph.notestash.common.coroutines.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

@Suppress("PropertyName")
interface DispatcherProvider {
    val Default: CoroutineDispatcher
    val Main: CoroutineDispatcher
    val MainImmediate: CoroutineDispatcher
    val IO: CoroutineDispatcher
    val Unconfined: CoroutineDispatcher
}