package domain.usecase

import domain.model.Item
import domain.repository.ItemRepository

class AddItemUseCase(private val repository: ItemRepository) {
    suspend operator fun invoke(item: Item): Long {
        return repository.addItem(item)
    }
}

