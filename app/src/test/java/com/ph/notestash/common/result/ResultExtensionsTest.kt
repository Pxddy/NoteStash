package com.ph.notestash.common.result

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import org.junit.jupiter.api.Test

internal class ResultExtensionsTest {

    @Test
    fun `checkCancellation() throws if failure was caused by cancellation`() {
        val cancellation = CancellationException("Test cancellation")
        val cancellationFailure: Result<Int> = Result.failure(cancellation)

        shouldThrow<CancellationException> {
            cancellationFailure.checkCancellation()
        } shouldBe cancellation

        val error = Exception("Test error")
        val errorFailure: Result<Int> = Result.failure(error)

        shouldNotThrowAny { errorFailure.checkCancellation() }
        errorFailure.exceptionOrNull() shouldBe error

        val success = Result.success(5)

        shouldNotThrowAny { success.checkCancellation() }
        success.exceptionOrNull() shouldBe null
    }
}