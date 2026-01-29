package data.repository

import data.local.dao.TransactionDao
import data.local.entity.TransactionEntity
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import domain.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionRepositoryDeleteTest {

    @Test
    fun `repository delete delegates to dao`() = runBlocking {
        val entity1 = TransactionEntity(
            id = 1,
            amountCents = 100,
            type = TransactionType.EXPENSE,
            description = "d",
            humanDate = "2026-01-01",
            createdAt = 1L,
        )
        val entity2 = TransactionEntity(
            id = 2,
            amountCents = 200,
            type = TransactionType.EXPENSE,
            description = "d",
            humanDate = "2026-01-02",
            createdAt = 2L,
        )

        val dao = mock<TransactionDao> {
            every { getAllTransactions() } returns flowOf(listOf(entity1, entity2))
            every { getTransactionsForMonth(any(), any()) } returns flowOf(listOf(entity1, entity2))
            everySuspend { getTransactionById(any()) } returns null
            everySuspend { addTransaction(any()) } returns 1L
            everySuspend { insertAll(any()) } returns listOf(1L)
            everySuspend { deleteTransactionById(1) } returns 1
            everySuspend { deleteAll() } returns 2
        }

        val repo = TransactionRepositoryImpl(dao)

        // After deletion, we need to update the mock to return only entity2
        every { dao.getAllTransactions() } returns flowOf(listOf(entity2))

        val deleted = repo.deleteTransaction(1)
        assertEquals(1, deleted)

        val all = repo.getTransactions().first()
        assertEquals(1, all.size)
        assertEquals(2, all[0].id)
    }
}
