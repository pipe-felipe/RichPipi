package data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import domain.model.TransactionType

@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amountCents: Long,
    val type: TransactionType = TransactionType.EXPENSE,
    val description: String? = null,
    val startDate: Long,
    val rule: RecurrenceRule = RecurrenceRule.MONTHLY,
    val interval: Int = 1, // every N rule units
    val endDate: Long? = null,
    val active: Boolean = true,
    val createdAt: Long = 0L
)
