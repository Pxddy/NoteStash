package com.ph.notestash.common.time

import java.time.Instant
import javax.inject.Inject

class TimeProvider @Inject constructor() {
    val now: Instant
        get() = Instant.now()
}