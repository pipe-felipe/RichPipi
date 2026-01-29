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

class AddItemUseCaseTest {

    @Test
    fun `add item returns id and stores item`() = runBlocking {
        val item = Transaction(
            id = 1,
            amountCents = 1000,
            type = TransactionType.INCOME,
            description = "desc",
            createdAt = 123L,
        )

        val repo = mock<TransactionRepository> {
            every { getTransactions() } returns flowOf(listOf(item))
            every { getTransactionsForMonth(any(), any()) } returns flowOf(emptyList())
            everySuspend { makeTransaction(any()) } returns 1L
            everySuspend { deleteTransaction(any()) } returns 1
            everySuspend { deleteAllTransactions() } returns 1
            everySuspend { insertTransactions(any()) } returns listOf(1L)
        }

        val useCase = MakeTransactionUseCase(repo)

        val id = useCase(item)

        assertEquals(1L, id)
        val all = repo.getTransactions()
        val first = all.first()
        assertEquals(1, first.size)
        assertEquals(1000, first[0].amountCents)
    }
}
