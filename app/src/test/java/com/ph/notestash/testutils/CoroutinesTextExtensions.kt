package com.ph.notestash.testutils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.TestScope

/**
 * Use this in cases where the [TestScope] should not be canceled if a child coroutine fails.
 */
internal suspend fun <T> runWithoutChildExceptionCancellation(
    action: suspend CoroutineScope.() -> T
) = supervisorScope(action)