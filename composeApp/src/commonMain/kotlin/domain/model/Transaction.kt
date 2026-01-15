package domain.model

import kotlin.jvm.JvmInline

enum class TransactionType { INCOME, EXPENSE }

data class Transaction(
    val id: Int = 0,
    val amountCents: Long,
    val type: TransactionType,
    val description: String? = null,
    val date: Long = 0L,
    val createdAt: Long = 0L
)
