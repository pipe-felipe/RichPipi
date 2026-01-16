package data.repository

import data.local.entity.TransactionEntity
import domain.model.Transaction
import domain.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionRepositoryMappingTest {
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
            } else false
            flow.value = list
            return if (removed) 1 else 0
        }
    }

    @Test
    fun `entity to domain mapping`() = runBlocking {
        val fakeDao = FakeDao()
        val repo = TransactionRepositoryImpl(
            object : data.local.dao.TransactionDao {
                override fun getAllTransactions() = fakeDao.getAllTransactions()

                override fun getTransactionsForMonth(
                    monthStartMillis: Long,
                    monthEndExclusiveMillis: Long
                ) = fakeDao.getAllTransactions()

                override suspend fun getTransactionById(id: Int) = fakeDao.list.find { it.id == id }
                override suspend fun addTransaction(item: TransactionEntity) = fakeDao.addTransaction(item)
                override suspend fun deleteTransactionById(id: Int) = fakeDao.deleteTransactionById(id)
            }
        )

        fakeDao.addTransaction(TransactionEntity(amountCents = 9999, type = domain.model.TransactionType.INCOME, description = "d", date = 1L, createdAt = 1L))
        val all = repo.getTransactions().first()
        assertEquals(1, all.size)
        assertEquals(9999, all[0].amountCents)
        assertEquals(TransactionType.INCOME, all[0].type)
    }
}
