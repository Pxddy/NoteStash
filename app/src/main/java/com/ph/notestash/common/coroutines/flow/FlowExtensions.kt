package com.ph.notestash.common.coroutines.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Converts a [Flow] into a [SharedFlow]. It replays the last emission to new subscribers.
 */
fun <T> Flow<T>.shareLatest(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(replayExpirationMillis = 0)
): SharedFlow<T> = conflate()
    .shareIn(scope = scope, started = started, replay = 1)

/**
 * This is a shorthand for `scope.launch { flow.collectLatest(action) }`
 */
inline fun <T> Flow<T>.collectLatestIn(
    scope: CoroutineScope,
    crossinline action: suspend (value: T) -> Unit
) = scope.launch {
    collectLatest { action(it) }
}