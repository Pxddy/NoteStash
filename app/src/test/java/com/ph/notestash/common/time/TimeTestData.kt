package com.ph.notestash.common.time

import java.time.Instant
import java.time.ZoneId

internal object TimeTestData {

    const val testInstantString = "2022-07-13T13:54:20.359290800Z"
    val testInstant: Instant = Instant.parse(testInstantString)
    val testZoneId: ZoneId = ZoneId.of("Europe/Berlin")
}