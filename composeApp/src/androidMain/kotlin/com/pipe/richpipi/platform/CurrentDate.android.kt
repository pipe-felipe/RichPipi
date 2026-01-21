package com.pipe.richpipi.platform

import java.util.Calendar
import java.util.Locale

actual fun currentDateString(): String {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    return String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
}
