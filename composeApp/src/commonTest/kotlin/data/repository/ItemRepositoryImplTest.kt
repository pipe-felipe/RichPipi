package data.repository

import data.local.entity.ItemEntity
import domain.model.Item
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ItemRepositoryImplTest {
    private class FakeDao {
        val list = mutableListOf<ItemEntity>()
        val flow = MutableStateFlow<List<ItemEntity>>(list)
        fun getAllItems() = flow as kotlinx.coroutines.flow.Flow<List<ItemEntity>>
        fun addItem(entity: ItemEntity): Long {
            list.add(entity.copy(id = list.size + 1))
            flow.value = list
            return list.size.toLong()
        }
    }

    @Test
    fun `repository maps entity to domain and adds`() = runBlocking {
        val fakeDao = FakeDao()
        val repo = ItemRepositoryImpl(
            object : data.local.dao.ItemDao {
                override fun getAllItems() = fakeDao.getAllItems()
                override suspend fun getItemById(id: Int) = fakeDao.list.find { it.id == id }
                override suspend fun addItem(item: ItemEntity) = fakeDao.addItem(item)
            }
        )

        val id = repo.addItem(Item(name = "RepoTest", description = "d", createdAt = 1L))
        assertEquals(1L, id)
        val all = repo.getAllItems().first()
        assertEquals(1, all.size)
        assertEquals("RepoTest", all[0].name)
    }
}
