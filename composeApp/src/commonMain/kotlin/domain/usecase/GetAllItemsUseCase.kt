package domain.usecase

import domain.model.Item
import domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow

class GetAllItemsUseCase(private val repository: ItemRepository) {
    operator fun invoke(): Flow<List<Item>> = repository.getAllItems()
}

