package domain.usecase

import domain.model.Transaction
import domain.repository.TransactionRepository

class MakeTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(item: Transaction): Long {
        return repository.makeTransaction(item)
    }
}
