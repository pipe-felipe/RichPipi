package com.pipe.richpipi.platform

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

actual fun currentDateString(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd"
    return formatter.stringFromDate(NSDate())
}
