package domain.usecase

import domain.model.Transaction
import domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactions(private val repository: TransactionRepository) {
    operator fun invoke(): Flow<List<Transaction>> = repository.getTransactions()
}

