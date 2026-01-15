package data.repository

import data.local.entity.TransactionEntity
import domain.model.Transaction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ItemRepositoryImplTest {
    private class FakeDao {
        val list = mutableListOf<TransactionEntity>()
        val flow = MutableStateFlow<List<TransactionEntity>>(list)
        fun getAllItems() = flow as kotlinx.coroutines.flow.Flow<List<TransactionEntity>>
        fun addItem(entity: TransactionEntity): Long {
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
    fun `repository maps entity to domain and adds`() = runBlocking {
        val fakeDao = FakeDao()
        val repo = TransactionRepositoryImpl(
            object : data.local.dao.ItemDao {
                override fun getAllItems() = fakeDao.getAllItems()
                override suspend fun getItemById(id: Int) = fakeDao.list.find { it.id == id }
                override suspend fun addItem(item: TransactionEntity) = fakeDao.addItem(item)
                override suspend fun deleteById(id: Int) = fakeDao.deleteById(id)
            }
        )

        val id = repo.makeTransaction(Transaction(value = "RepoTest", description = "d", createdAt = 1L))
        assertEquals(1L, id)
        val all = repo.getTransactions().first()
        assertEquals(1, all.size)
        assertEquals("RepoTest", all[0].value)
    }
}
