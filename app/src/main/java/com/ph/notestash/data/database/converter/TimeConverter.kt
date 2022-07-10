package com.ph.notestash.data.database.converter

import androidx.room.TypeConverter
import java.time.Instant

class TimeConverter {

    @TypeConverter
    fun toInstant(value: String?): Instant? = value?.let { Instant.parse(it) }

    @TypeConverter
    fun fromInstant(instant: Instant?): String? = instant?.toString()
}