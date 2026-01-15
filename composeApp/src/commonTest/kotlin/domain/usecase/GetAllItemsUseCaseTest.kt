package domain.usecase

import domain.model.Transaction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllItemsUseCaseTest {
    private class FakeRepo : domain.repository.TransactionRepository {
        val items = mutableListOf<Transaction>()
        override fun getTransactions() = MutableStateFlow(items) as kotlinx.coroutines.flow.Flow<List<Transaction>>
        override suspend fun makeTransaction(item: Transaction): Long {
            items.add(item.copy(id = items.size + 1))
            return items.size.toLong()
        }
        override suspend fun deleteTransaction(id: Int): Int {
            val idx = items.indexOfFirst { it.id == id }
            return if (idx >= 0) { items.removeAt(idx); 1 } else 0
        }
    }

    @Test
    fun `getAllItems returns flow of items`() = runBlocking {
        val repo = FakeRepo()
        val useCase = GetTransactions(repo)

        // initially empty
        val initial = useCase().first()
        assertEquals(0, initial.size)

        // add item and verify
        repo.makeTransaction(Transaction(value = "A", description = "d", createdAt = 1L))
        val after = useCase().first()
        assertEquals(1, after.size)
        assertEquals("A", after[0].value)
    }
}
