package com.pipe.richpipi.platform

/**
 * Provides current month/year in common code.
 *
 * Contract:
 * - month: 1..12
 * - year: full year (e.g., 2026)
 */
expect fun currentMonthYear(): Pair<Int, Int>
