package com.pipe.richpipi.platform

/**
 * Returns [startMillis, endExclusiveMillis) for the given month/year in the current device timezone.
 */
expect fun monthBoundsUtcMillis(month: Int, year: Int): Pair<Long, Long>
