package com.ph.notestash.common.time

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

internal class DateAndTimeExtensionsTest {

    @Test
    fun `toLocalDateTime forwards instant and zoneId`() {
        val localDateTime1 = TimeTestData.testInstant.toLocalDateTime(TimeTestData.testZoneId)
        val localDateTime2 =
            LocalDateTime.ofInstant(TimeTestData.testInstant, TimeTestData.testZoneId)
        localDateTime1 shouldBe localDateTime2
    }

    @Test
    fun `check time formatting`() {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
            .withLocale(Locale.ENGLISH)

        val expectedOutput = "July 13, 2022"

        mockkStatic(DateTimeFormatter::class) {
            every { DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG) } returns formatter

            val output1 = TimeTestData.testInstant.toLongDateFormat(TimeTestData.testZoneId)
            val output2 = TimeTestData.testInstant.toLocalDateTime(TimeTestData.testZoneId)
                .longDateFormat

            output1 shouldBe expectedOutput
            output2 shouldBe expectedOutput
        }
    }
}