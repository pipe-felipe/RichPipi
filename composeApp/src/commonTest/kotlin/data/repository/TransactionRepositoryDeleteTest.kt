package data.repository

import data.local.entity.TransactionEntity
import domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionRepositoryDeleteTest {
    private class FakeDao {
        val list = mutableListOf<TransactionEntity>()
        val flow = MutableStateFlow<List<TransactionEntity>>(list)
        fun getAllTransactions() = flow as kotlinx.coroutines.flow.Flow<List<TransactionEntity>>
        fun addTransaction(entity: TransactionEntity): Long {
            list.add(entity.copy(id = list.size + 1))
            flow.value = list
            return list.size.toLong()
        }

        fun deleteTransactionById(id: Int): Int {
            val idx = list.indexOfFirst { it.id == id }
            val removed = if (idx >= 0) {
                list.removeAt(idx)
                true
            } else {
                false
            }
            flow.value = list
            return if (removed) 1 else 0
        }
    }

    @Test
    fun `repository delete delegates to dao`() = runBlocking {
        val fakeDao = FakeDao()
        // pre-populate -- now use amountCents, type, humanDate, createdAt
        fakeDao.addTransaction(TransactionEntity(amountCents = 100, type = TransactionType.EXPENSE, description = "d", humanDate = "2026-01-01", createdAt = 1L))
        fakeDao.addTransaction(TransactionEntity(amountCents = 200, type = TransactionType.EXPENSE, description = "d", humanDate = "2026-01-02", createdAt = 2L))

        val repo = TransactionRepositoryImpl(
            object : data.local.dao.TransactionDao {
                override fun getAllTransactions() = fakeDao.getAllTransactions()

                override fun getTransactionsForMonth(
                    monthStartMillis: Long,
                    monthEndExclusiveMillis: Long,
                ) = fakeDao.getAllTransactions()

                override suspend fun getTransactionById(id: Int) = fakeDao.list.find { it.id == id }
                override suspend fun addTransaction(item: TransactionEntity) = fakeDao.addTransaction(item)
                override suspend fun insertAll(items: List<TransactionEntity>): List<Long> {
                    return items.map { fakeDao.addTransaction(it) }
                }
                override suspend fun deleteTransactionById(id: Int) = fakeDao.deleteTransactionById(id)
                override suspend fun deleteAll(): Int {
                    val count = fakeDao.list.size
                    fakeDao.list.clear()
                    return count
                }
            },
        )

        val deleted = repo.deleteTransaction(1)
        assertEquals(1, deleted)
        val all = repo.getTransactions().first()
        assertEquals(1, all.size)
        assertEquals(2, all[0].id)
    }
}
