package com.pipe.richpipi.domain.usecase

import com.pipe.richpipi.domain.model.Transaction
import com.pipe.richpipi.domain.repository.TransactionRepository

class InsertTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.insertTransaction(transaction)
    }
}