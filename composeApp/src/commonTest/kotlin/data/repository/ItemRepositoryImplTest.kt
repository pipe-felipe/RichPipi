package data.repository

import data.local.dao.TransactionDao
import data.local.entity.TransactionEntity
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import domain.model.Transaction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import domain.model.TransactionType as DomainTransactionType

class ItemRepositoryImplTest {

    @Test
    fun `repository maps entity to domain and adds`() = runBlocking {
        val addedEntity = TransactionEntity(
            id = 1,
            amountCents = 12345,
            type = DomainTransactionType.INCOME,
            description = "d",
            humanDate = "",
            isRecurring = false,
            createdAt = 1L,
        )

        val dao = mock<TransactionDao> {
            every { getAllTransactions() } returns flowOf(listOf(addedEntity))
            every { getTransactionsForMonth(any(), any()) } returns flowOf(emptyList())
            everySuspend { getTransactionById(any()) } returns null
            everySuspend { addTransaction(any()) } returns 1L
            everySuspend { deleteTransactionById(any()) } returns 1
            everySuspend { insertAll(any()) } returns emptyList()
            everySuspend { deleteAll() } returns 0
        }

        val repo = TransactionRepositoryImpl(dao)

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
        val entity = TransactionEntity(
            id = 1,
            amountCents = 999,
            type = DomainTransactionType.EXPENSE,
            description = "m",
            humanDate = "2026-01-10",
            isRecurring = false,
            createdAt = 5L,
        )

        val startCapture = Capture.slot<Long>()
        val endCapture = Capture.slot<Long>()

        val dao = mock<TransactionDao> {
            every { getAllTransactions() } returns flowOf(listOf(entity))
            every { getTransactionsForMonth(capture(startCapture), capture(endCapture)) } returns flowOf(listOf(entity))
            everySuspend { getTransactionById(any()) } returns entity
            everySuspend { addTransaction(any()) } returns 1L
            everySuspend { deleteTransactionById(any()) } returns 1
            everySuspend { insertAll(any()) } returns emptyList()
            everySuspend { deleteAll() } returns 0
        }

        val repo = TransactionRepositoryImpl(dao)

        val result = repo.getTransactionsForMonth(monthStartMillis = 1L, monthEndExclusiveMillis = 100L).first()
        assertEquals(1L, startCapture.get())
        assertEquals(100L, endCapture.get())
        assertEquals(1, result.size)
        assertEquals(999, result[0].amountCents)
        assertEquals(DomainTransactionType.EXPENSE, result[0].type)
        assertEquals("m", result[0].description)
    }

    @Test
    fun `repository delete delegates to dao`() = runBlocking {
        val entity = TransactionEntity(
            id = 1,
            amountCents = 1,
            type = DomainTransactionType.INCOME,
            description = null,
            humanDate = "2026-01-01",
            isRecurring = false,
            createdAt = 1L,
        )

        val deleteCapture = Capture.slot<Int>()

        val dao = mock<TransactionDao> {
            every { getAllTransactions() } returns flowOf(listOf(entity))
            every { getTransactionsForMonth(any(), any()) } returns flowOf(listOf(entity))
            everySuspend { getTransactionById(any()) } returns entity
            everySuspend { addTransaction(any()) } returns 1L
            everySuspend { deleteTransactionById(capture(deleteCapture)) } returns 1
            everySuspend { insertAll(any()) } returns emptyList()
            everySuspend { deleteAll() } returns 0
        }

        val repo = TransactionRepositoryImpl(dao)

        val rows = repo.deleteTransaction(1)
        assertEquals(1, rows)
        assertEquals(1, deleteCapture.get())
    }
}
