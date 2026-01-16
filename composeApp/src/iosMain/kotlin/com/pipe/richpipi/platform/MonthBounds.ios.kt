package com.pipe.richpipi.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.mktime
import platform.posix.tm

@OptIn(ExperimentalForeignApi::class)
actual fun monthBoundsUtcMillis(month: Int, year: Int): Pair<Long, Long> {
    require(month in 1..12)

    fun monthStartMillis(m: Int, y: Int): Long = memScoped {
        // Build a tm struct in local time at midnight of the first day
        val tmStruct = alloc<tm>()
        tmStruct.tm_year = y - 1900
        tmStruct.tm_mon = m - 1
        tmStruct.tm_mday = 1
        tmStruct.tm_hour = 0
        tmStruct.tm_min = 0
        tmStruct.tm_sec = 0
        tmStruct.tm_isdst = -1

        val seconds = mktime(tmStruct.ptr)
        seconds * 1000L
    }

    val start = monthStartMillis(month, year)
    val endExclusive = if (month == 12) monthStartMillis(1, year + 1) else monthStartMillis(month + 1, year)

    return start to endExclusive
}
