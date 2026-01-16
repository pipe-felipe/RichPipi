package data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import domain.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // store money as cents to avoid floating point issues
    val amountCents: Long,
    val type: TransactionType = TransactionType.EXPENSE,
    val description: String? = null,
    // occurrence date (epoch millis)
    val date: Long,
    // Marks whether this transaction should appear in every month.
    // (Repeatable expenses/income)
    val isRecurring: Boolean = false,
    // if this transaction was generated from a recurring rule, link to it
    val recurringId: Int? = null,
    // use 0L as default in commonMain; populate when creating instances
    val createdAt: Long = 0L
)
