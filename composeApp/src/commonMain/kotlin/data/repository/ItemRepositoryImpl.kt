package data.repository

import data.local.dao.ItemDao
import data.local.entity.ItemEntity
import domain.model.Item
import domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ItemRepositoryImpl(private val dao: ItemDao) : ItemRepository {
    override fun getAllItems(): Flow<List<Item>> {
        return dao.getAllItems().map { list ->
            list.map { entity ->
                Item(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun addItem(item: Item): Long {
        val entity = ItemEntity(
            name = item.name,
            description = item.description,
            createdAt = item.createdAt
        )
        return dao.addItem(entity)
    }

    override suspend fun deleteItem(id: Int): Int {
        return dao.deleteById(id)
    }
}
