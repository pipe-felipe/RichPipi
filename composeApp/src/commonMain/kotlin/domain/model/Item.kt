package domain.model

data class Item(
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val createdAt: Long = 0L
)

