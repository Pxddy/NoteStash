package com.ph.notestash.common.coroutines.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.StateFlow

/**
 * Converts the [Flow] into a [StateFlow] with null as the initial value. [filterNotNull] is used to
 * return a flow which does not contain the null emission.
 */
fun <T : Any> Flow<T>.shareLatest(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(replayExpirationMillis = 0)
): Flow<T> = stateIn(scope = scope, started = started, initialValue = null)
    .filterNotNull()