package domain.model

enum class TransactionType { INCOME, EXPENSE }

data class Transaction(
    val id: Int = 0,
    val amountCents: Long,
    val type: TransactionType,
    val category: String? = null,
    val description: String? = null,
    val humanDate: String = "",
    val isRecurring: Boolean = false,
    val createdAt: Long = 0L,
    /** Target month (1-12) where this transaction belongs */
    val targetMonth: Int = 0,
    /** Target year (e.g., 2026) where this transaction belongs */
    val targetYear: Int = 0,
)
