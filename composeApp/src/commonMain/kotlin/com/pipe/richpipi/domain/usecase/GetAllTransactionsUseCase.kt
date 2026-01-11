package com.pipe.richpipi.domain.usecase

import com.pipe.richpipi.domain.repository.TransactionRepository
import com.pipe.richpipi.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

class GetAllTransactionsUseCase(private val repository: TransactionRepository) {
    operator fun invoke(): Flow<List<Transaction>> = repository.getAllTransactions()
}