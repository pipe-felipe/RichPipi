package domain.usecase

import domain.repository.ItemRepository

class DeleteItemUseCase(private val repository: ItemRepository) {
    suspend operator fun invoke(id: Int): Int {
        return repository.deleteItem(id)
    }
}

