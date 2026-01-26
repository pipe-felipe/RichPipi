package data.repository

import data.local.entity.TransactionEntity
import domain.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import domain.model.TransactionType as DomainTransactionType

class ItemRepositoryImplTest {
    private class FakeDao {
        val list = mutableListOf<TransactionEntity>()
        val flow = MutableStateFlow<List<TransactionEntity>>(list)
        fun getAllItems() = flow as kotlinx.coroutines.flow.Flow<List<TransactionEntity>>

        var lastMonthStart: Long? = null
        var lastMonthEndExclusive: Long? = null

        fun getForMonth(start: Long, endExclusive: Long) = flow.also {
            lastMonthStart = start
            lastMonthEndExclusive = endExclusive
        } as kotlinx.coroutines.flow.Flow<List<TransactionEntity>>

        fun addItem(entity: TransactionEntity): Long {
            list.add(entity.copy(id = list.size + 1))
            flow.value = list
            return list.size.toLong()
        }

        var lastDeletedId: Int? = null

        fun deleteById(id: Int): Int {
            lastDeletedId = id
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
    fun `repository maps entity to domain and adds`() = runBlocking {
        val fakeDao = FakeDao()
        val repo = TransactionRepositoryImpl(
            object : data.local.dao.TransactionDao {
                override fun getAllTransactions() = fakeDao.getAllItems()

                override fun getTransactionsForMonth(
                    monthStartMillis: Long,
                    monthEndExclusiveMillis: Long,
                ) = fakeDao.getAllItems()

                override suspend fun getTransactionById(id: Int) = fakeDao.list.find { it.id == id }
                override suspend fun addTransaction(item: TransactionEntity) = fakeDao.addItem(item)
                override suspend fun deleteTransactionById(id: Int) = fakeDao.deleteById(id)
                override suspend fun insertAll(items: List<TransactionEntity>): List<Long> = emptyList()
                override suspend fun deleteAll(): Int = 0
            },
        )

        val id = repo.makeTransaction(Transaction(amountCents = 12345, type = DomainTransactionType.INCOME, description = "d", createdAt = 1L))
        assertEquals(1L, id)
        val all = repo.getTransactions().first()
        assertEquals(1, all.size)
        assertEquals(1, all[0].id)
        assertEquals(12345, all[0].amountCents)
        assertEquals(DomainTransactionType.INCOME, all[0].type)
    }

    @Test
    fun `repository maps entity to domain for month query`() = runBlocking {
        val fakeDao = FakeDao()
        // seed 1 entity
        fakeDao.addItem(
            TransactionEntity(
                amountCents = 999,
                type = DomainTransactionType.EXPENSE,
                description = "m",
                humanDate = "2026-01-10",
                isRecurring = false,
                createdAt = 5L,
            ),
        )

        val repo = TransactionRepositoryImpl(
            object : data.local.dao.TransactionDao {
                override fun getAllTransactions() = fakeDao.getAllItems()

                override fun getTransactionsForMonth(monthStartMillis: Long, monthEndExclusiveMillis: Long) =
                    fakeDao.getForMonth(monthStartMillis, monthEndExclusiveMillis)

                override suspend fun getTransactionById(id: Int) = fakeDao.list.find { it.id == id }
                override suspend fun addTransaction(item: TransactionEntity) = fakeDao.addItem(item)
                override suspend fun deleteTransactionById(id: Int) = fakeDao.deleteById(id)
                override suspend fun insertAll(items: List<TransactionEntity>): List<Long> = emptyList()
                override suspend fun deleteAll(): Int = 0
            },
        )

        val result = repo.getTransactionsForMonth(monthStartMillis = 1L, monthEndExclusiveMillis = 100L).first()
        assertEquals(1L, fakeDao.lastMonthStart)
        assertEquals(100L, fakeDao.lastMonthEndExclusive)
        assertEquals(1, result.size)
        assertEquals(999, result[0].amountCents)
        assertEquals(DomainTransactionType.EXPENSE, result[0].type)
        assertEquals("m", result[0].description)
    }

    @Test
    fun `repository delete delegates to dao`() = runBlocking {
        val fakeDao = FakeDao()
        // insert so delete returns 1
        fakeDao.addItem(
            TransactionEntity(
                amountCents = 1,
                type = DomainTransactionType.INCOME,
                description = null,
                humanDate = "2026-01-01",
                isRecurring = false,
                createdAt = 1L,
            ),
        )

        val repo = TransactionRepositoryImpl(
            object : data.local.dao.TransactionDao {
                override fun getAllTransactions() = fakeDao.getAllItems()

                override fun getTransactionsForMonth(monthStartMillis: Long, monthEndExclusiveMillis: Long) =
                    fakeDao.getAllItems()

                override suspend fun getTransactionById(id: Int) = fakeDao.list.find { it.id == id }
                override suspend fun addTransaction(item: TransactionEntity) = fakeDao.addItem(item)
                override suspend fun deleteTransactionById(id: Int) = fakeDao.deleteById(id)
                override suspend fun insertAll(items: List<TransactionEntity>): List<Long> = emptyList()
                override suspend fun deleteAll(): Int = 0
            },
        )

        val rows = repo.deleteTransaction(1)
        assertEquals(1, rows)
        assertEquals(1, fakeDao.lastDeletedId)
    }
}
