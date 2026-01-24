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
)
