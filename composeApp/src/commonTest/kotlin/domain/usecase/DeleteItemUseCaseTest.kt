package domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking

class DeleteItemUseCaseTest {
    private class FakeRepo(var deletedId: Int? = null) : domain.repository.TransactionRepository {
        override fun getTransactions() = throw UnsupportedOperationException()
        override suspend fun makeTransaction(item: domain.model.Transaction) = throw UnsupportedOperationException()
        override suspend fun deleteTransaction(id: Int): Int {
            deletedId = id
            return 1
        }
    }

    @Test
    fun `delete usecase delegates to repository`() = runBlocking {
        val fakeRepo = FakeRepo()
        val uc = DeleteTransactionUseCase(fakeRepo)
        val result = uc(42)
        assertEquals(1, result)
        assertEquals(42, fakeRepo.deletedId)
    }
}

