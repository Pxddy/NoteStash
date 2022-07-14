package com.ph.notestash.common.time

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

internal class TimeProviderTest {

    @Test
    fun `time provider now returns instant now`() {
        val timeProvider = TimeProvider()
        mockkStatic(Instant::class) {
            every { Instant.now() } returns TimeTestData.testInstant
            timeProvider.now shouldBe TimeTestData.testInstant

            val testInstantPlus = TimeTestData.testInstant.plus(1, ChronoUnit.HOURS)
            every { Instant.now() } returns testInstantPlus
            timeProvider.now shouldBe testInstantPlus
        }
    }
}