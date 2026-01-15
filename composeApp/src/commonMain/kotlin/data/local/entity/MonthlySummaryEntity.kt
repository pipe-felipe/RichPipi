package data.local.entity

import androidx.room.Entity

@Entity(tableName = "monthly_summaries", primaryKeys = ["year", "month"])
data class MonthlySummaryEntity(
    val year: Int,
    val month: Int, // 1..12
    val totalIncomeCents: Long = 0L,
    val totalExpenseCents: Long = 0L,
    val startingBalanceCents: Long = 0L,
    val endingBalanceCents: Long = 0L,
    val updatedAt: Long = 0L
)
