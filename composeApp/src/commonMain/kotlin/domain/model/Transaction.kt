package domain.model

enum class TransactionType { INCOME, EXPENSE }

data class Transaction(
    val id: Int = 0,
    val amountCents: Long,
    val type: TransactionType,
    val description: String? = null,
    val date: Long = 0L,
    val isRecurring: Boolean = false,
    val createdAt: Long = 0L
)
