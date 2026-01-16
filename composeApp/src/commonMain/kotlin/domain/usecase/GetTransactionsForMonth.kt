package domain.usecase

import domain.model.Transaction
import domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsForMonth(private val repository: TransactionRepository) {
    operator fun invoke(monthStartMillis: Long, monthEndExclusiveMillis: Long): Flow<List<Transaction>> =
        repository.getTransactionsForMonth(monthStartMillis, monthEndExclusiveMillis)
}
