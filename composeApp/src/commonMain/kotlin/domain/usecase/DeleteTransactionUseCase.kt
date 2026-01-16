package domain.usecase

import domain.repository.TransactionRepository

class DeleteTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(id: Int): Int {
        return repository.deleteTransaction(id)
    }
}

