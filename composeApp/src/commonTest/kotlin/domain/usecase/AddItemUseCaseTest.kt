package domain.usecase

import domain.model.Item
import domain.repository.ItemRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlin.test.Test
import kotlin.test.assertEquals

class AddItemUseCaseTest {
    private class FakeRepo : ItemRepository {
        val items = mutableListOf<Item>()
        override fun getAllItems() = kotlinx.coroutines.flow.MutableStateFlow(items) as kotlinx.coroutines.flow.Flow<List<Item>>
        override suspend fun addItem(item: Item): Long {
            items.add(item.copy(id = items.size + 1))
            return items.size.toLong()
        }
    }

    @Test
    fun `add item returns id and stores item`() = runBlocking {
        val repo = FakeRepo()
        val useCase = AddItemUseCase(repo)

        val item = Item(name = "Test", description = "desc", createdAt = 123L)
        val id = useCase(item)

        assertEquals(1L, id)
        val all = repo.getAllItems()
        val first = all.first()
        assertEquals(1, first.size)
        assertEquals("Test", first[0].name)
    }
}
