package domain.model

data class Transaction(
    val id: Int = 0,
    val value: String,
    val description: String? = null,
    val createdAt: Long = 0L
)

