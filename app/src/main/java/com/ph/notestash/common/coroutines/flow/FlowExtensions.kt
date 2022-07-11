package com.ph.notestash.common.coroutines.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Converts the [Flow] into a [StateFlow] with null as the initial value. [filterNotNull] is used to
 * return a flow which does not contain the null emission.
 */
fun <T : Any> Flow<T>.shareLatest(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(replayExpirationMillis = 0)
): Flow<T> = stateIn(scope = scope, started = started, initialValue = null)
    .filterNotNull()

/**
 * This is a shorthand for `scope.launch { flow.collectLatest(action) }`
 */
fun <T> Flow<T>.collectLatestIn(
    scope: CoroutineScope,
    action: suspend (value: T) -> Unit
) = scope.launch {
    collectLatest(action)
}