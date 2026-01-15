package com.pipe.richpipi.platform

import java.util.Calendar

actual fun currentMonthYear(): Pair<Int, Int> {
    val calendar = Calendar.getInstance()
    val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
    val year = calendar.get(Calendar.YEAR)
    return month to year
}
