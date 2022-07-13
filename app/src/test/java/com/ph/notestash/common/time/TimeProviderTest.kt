package com.ph.notestash.common.time

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Test
import java.time.Instant

internal class TimeProviderTest {

    @Test
    fun `time provider now returns instant now`() {
        mockkStatic(Instant::class) {
            every { Instant.now() } returns TimeTestData.testInstant
            TimeProvider().now shouldBe TimeTestData.testInstant
        }
    }
}