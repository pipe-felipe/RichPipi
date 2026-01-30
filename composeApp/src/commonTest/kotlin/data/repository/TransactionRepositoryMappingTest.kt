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

class TransactionRepositoryMappingTest {

    @Test
    fun `entity to domain mapping`() = runBlocking {
        val entity = TransactionEntity(
            id = 1,
            amountCents = 9999,
            type = TransactionType.INCOME,
            description = "d",
            humanDate = "2026-01-01",
            createdAt = 1L,
        )

        val dao = mock<TransactionDao> {
            every { getAllTransactions() } returns flowOf(listOf(entity))
            every { getTransactionsForMonth(any(), any()) } returns flowOf(listOf(entity))
            everySuspend { getTransactionById(any()) } returns entity
            everySuspend { addTransaction(any()) } returns 1L
            everySuspend { insertAll(any()) } returns listOf(1L)
            everySuspend { deleteTransactionById(any()) } returns 1
            everySuspend { deleteAll() } returns 1
        }

        val repo = TransactionRepositoryImpl(dao)

        val all = repo.getTransactions().first()
        assertEquals(1, all.size)
        assertEquals(9999, all[0].amountCents)
        assertEquals(TransactionType.INCOME, all[0].type)
    }
}
