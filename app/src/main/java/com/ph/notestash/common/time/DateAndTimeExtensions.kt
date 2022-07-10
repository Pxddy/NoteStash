package com.ph.notestash.common.time

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun Instant.toLocalDateTime(
    zoneId: ZoneId = ZoneId.systemDefault()
): LocalDateTime = LocalDateTime.ofInstant(this, zoneId)

val LocalDateTime.longDateFormat: String
    get() = format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

fun Instant.toLongDateFormat(
    zoneId: ZoneId = ZoneId.systemDefault()
) = toLocalDateTime(zoneId).longDateFormat