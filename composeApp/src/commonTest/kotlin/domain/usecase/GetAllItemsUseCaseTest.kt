package domain.usecase

import domain.model.Item
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllItemsUseCaseTest {
    private class FakeRepo : domain.repository.ItemRepository {
        val items = mutableListOf<Item>()
        override fun getAllItems() = MutableStateFlow(items) as kotlinx.coroutines.flow.Flow<List<Item>>
        override suspend fun addItem(item: Item): Long {
            items.add(item.copy(id = items.size + 1))
            return items.size.toLong()
        }
    }

    @Test
    fun `getAllItems returns flow of items`() = runBlocking {
        val repo = FakeRepo()
        val useCase = GetAllItemsUseCase(repo)

        // initially empty
        val initial = useCase().first()
        assertEquals(0, initial.size)

        // add item and verify
        repo.addItem(Item(name = "A", description = "d", createdAt = 1L))
        val after = useCase().first()
        assertEquals(1, after.size)
        assertEquals("A", after[0].name)
    }
}
