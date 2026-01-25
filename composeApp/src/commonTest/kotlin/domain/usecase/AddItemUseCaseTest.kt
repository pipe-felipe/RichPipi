package domain.usecase

import domain.model.Transaction
import domain.model.TransactionType
import domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class AddItemUseCaseTest {
    private class FakeRepo : TransactionRepository {
        val items = mutableListOf<Transaction>()
        override fun getTransactions() = kotlinx.coroutines.flow.MutableStateFlow(items) as kotlinx.coroutines.flow.Flow<List<Transaction>>

        override fun getTransactionsForMonth(monthStartMillis: Long, monthEndExclusiveMillis: Long) =
            getTransactions()

        override suspend fun makeTransaction(item: Transaction): Long {
            items.add(item.copy(id = items.size + 1))
            return items.size.toLong()
        }
        override suspend fun deleteTransaction(id: Int): Int {
            val idx = items.indexOfFirst { it.id == id }
            return if (idx >= 0) {
                items.removeAt(idx)
                1
            } else {
                0
            }
        }

        override suspend fun deleteAllTransactions(): Int {
            val count = items.size
            items.clear()
            return count
        }

        override suspend fun insertTransactions(transactions: List<Transaction>): List<Long> {
            return transactions.mapIndexed { index, transaction ->
                items.add(transaction.copy(id = items.size + 1))
                (items.size).toLong()
            }
        }
    }

    @Test
    fun `add item returns id and stores item`() = runBlocking {
        val repo = FakeRepo()
        val useCase = MakeTransactionUseCase(repo)

        val item = Transaction(amountCents = 1000, type = TransactionType.INCOME, description = "desc", createdAt = 123L)
        val id = useCase(item)

        assertEquals(1L, id)
        val all = repo.getTransactions()
        val first = all.first()
        assertEquals(1, first.size)
        assertEquals(1000, first[0].amountCents)
    }
}
