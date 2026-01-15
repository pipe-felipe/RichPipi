package domain.repository

import domain.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getAllItems(): Flow<List<Item>>
    suspend fun addItem(item: Item): Long
    suspend fun deleteItem(id: Int): Int
}
