package com.pipe.richpipi.platform

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate

actual fun currentMonthYear(): Pair<Int, Int> {
    val date = NSDate()
    val calendar = NSCalendar.currentCalendar
    val components = calendar.components(NSCalendarUnitMonth or NSCalendarUnitYear, fromDate = date)

    val month = components.month.toInt()
    val year = components.year.toInt()

    // month is expected 1..12
    return month to year
}
