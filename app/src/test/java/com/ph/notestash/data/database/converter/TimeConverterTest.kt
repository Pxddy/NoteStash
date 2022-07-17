package com.ph.notestash.data.database.converter

import com.ph.notestash.common.time.TimeTestData
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class TimeConverterTest {

    private val instance = TimeConverter()

    @Test
    fun `instant conversion`() {
        val instant = TimeTestData.testInstant
        val raw = TimeTestData.testInstantString

        with(instance) {
            fromInstant(instant) shouldBe raw
            toInstant(raw) shouldBe instant

            fromInstant(instant = null) shouldBe null
            toInstant(value = null) shouldBe null
        }
    }
}