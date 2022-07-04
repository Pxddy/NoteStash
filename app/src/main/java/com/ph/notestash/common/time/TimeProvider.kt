package com.ph.notestash.common.time

import dagger.Reusable
import java.time.Instant
import javax.inject.Inject

@Reusable
class TimeProvider @Inject constructor() {
    val now: Instant
        get() = Instant.now()
}