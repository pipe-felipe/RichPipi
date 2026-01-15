package com.pipe.richpipi.platform

import java.util.Calendar
import java.util.TimeZone

actual fun monthBoundsUtcMillis(month: Int, year: Int): Pair<Long, Long> {
    require(month in 1..12)

    val tz = TimeZone.getDefault()

    val start = Calendar.getInstance(tz).apply {
        clear()
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val endExclusive = Calendar.getInstance(tz).apply {
        timeInMillis = start
        add(Calendar.MONTH, 1)
    }.timeInMillis

    return start to endExclusive
}
