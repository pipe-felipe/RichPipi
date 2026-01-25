package data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import domain.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amountCents: Long,
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String? = null,
    val description: String? = null,
    val humanDate: String? = null,
    val isRecurring: Boolean = false,
    val createdAt: Long = 0L,
    /** Target month (1-12) where this transaction belongs */
    val targetMonth: Int = 0,
    /** Target year (e.g., 2026) where this transaction belongs */
    val targetYear: Int = 0,
)
