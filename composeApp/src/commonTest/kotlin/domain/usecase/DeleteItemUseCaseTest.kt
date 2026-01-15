package domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking

class DeleteItemUseCaseTest {
    private class FakeRepo(var deletedId: Int? = null) : domain.repository.ItemRepository {
        override fun getAllItems() = throw UnsupportedOperationException()
        override suspend fun addItem(item: domain.model.Item) = throw UnsupportedOperationException()
        override suspend fun deleteItem(id: Int): Int {
            deletedId = id
            return 1
        }
    }

    @Test
    fun `delete usecase delegates to repository`() = runBlocking {
        val fakeRepo = FakeRepo()
        val uc = DeleteItemUseCase(fakeRepo)
        val result = uc(42)
        assertEquals(1, result)
        assertEquals(42, fakeRepo.deletedId)
    }
}

