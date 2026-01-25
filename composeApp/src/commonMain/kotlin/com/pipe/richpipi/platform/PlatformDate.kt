package com.pipe.richpipi.platform

/**
 * Provides current month/year in common code.
 *
 * Contract:
 * - month: 1..12
 * - year: full year (e.g., 2026)
 */
expect fun currentMonthYear(): Pair<Int, Int>

/**
 * Returns the exclusive end timestamp (in UTC millis) for the current month.
 * This is equivalent to the start of the next month.
 *
 * Used to determine which transactions should be considered "up to current month"
 * for savings calculations (future income should not be counted).
 */
fun currentMonthEndExclusiveMillis(): Long {
    val (month, year) = currentMonthYear()
    val (_, endExclusive) = monthBoundsUtcMillis(month, year)
    return endExclusive
}
