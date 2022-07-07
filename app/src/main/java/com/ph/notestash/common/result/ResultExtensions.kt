package com.ph.notestash.common.result

import kotlinx.coroutines.CancellationException

/**
 * Checks if this instance represents a [failure][Result.failure] and if the failure was caused by
 * cancellation it throws the corresponding [CancellationException].
 */
fun <T> Result<T>.checkCancellation() = onFailure { if (it is CancellationException) throw it }