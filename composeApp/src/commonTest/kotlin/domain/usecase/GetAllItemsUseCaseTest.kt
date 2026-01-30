package domain.usecase

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import domain.model.Transaction
import domain.model.TransactionType
import domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllItemsUseCaseTest {

    @Test
    fun `getAllItems returns flow of items`() = runBlocking {
        val transaction = Transaction(
            id = 1,
            amountCents = 100,
            type = TransactionType.INCOME,
            description = "d",
            createdAt = 1L,
        )

        val repo = mock<TransactionRepository> {
            every { getTransactions() } returns flowOf(listOf(transaction))
            every { getTransactionsForMonth(any(), any()) } returns flowOf(emptyList())
            everySuspend { makeTransaction(any()) } returns 1L
            everySuspend { deleteTransaction(any()) } returns 1
            everySuspend { deleteAllTransactions() } returns 1
            everySuspend { insertTransactions(any()) } returns listOf(1L)
        }

        val useCase = GetTransactions(repo)

        val result = useCase().first()
        assertEquals(1, result.size)
        assertEquals(100, result[0].amountCents)
    }
}
