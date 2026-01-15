package data.repository

import data.local.entity.ItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ItemRepositoryDeleteTest {
    private class FakeDao {
        val list = mutableListOf<ItemEntity>()
        val flow = MutableStateFlow<List<ItemEntity>>(list)
        fun getAllItems() = flow as kotlinx.coroutines.flow.Flow<List<ItemEntity>>
        fun addItem(entity: ItemEntity): Long {
            list.add(entity.copy(id = list.size + 1))
            flow.value = list
            return list.size.toLong()
        }

        fun deleteById(id: Int): Int {
            val idx = list.indexOfFirst { it.id == id }
            val removed = if (idx >= 0) {
                list.removeAt(idx)
                true
            } else false
            flow.value = list
            return if (removed) 1 else 0
        }
    }

    @Test
    fun `repository delete delegates to dao`() = runBlocking {
        val fakeDao = FakeDao()
        // pre-populate
        fakeDao.addItem(ItemEntity(name = "a", description = "d", createdAt = 1L))
        fakeDao.addItem(ItemEntity(name = "b", description = "d", createdAt = 2L))

        val repo = ItemRepositoryImpl(
            object : data.local.dao.ItemDao {
                override fun getAllItems() = fakeDao.getAllItems()
                override suspend fun getItemById(id: Int) = fakeDao.list.find { it.id == id }
                override suspend fun addItem(item: ItemEntity) = fakeDao.addItem(item)
                override suspend fun deleteById(id: Int) = fakeDao.deleteById(id)
            }
        )

        val deleted = repo.deleteItem(1)
        assertEquals(1, deleted)
        val all = repo.getAllItems().first()
        assertEquals(1, all.size)
        assertEquals("b", all[0].name)
    }
}
