package com.pipe.richpipi.domain.repository

import com.pipe.richpipi.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Clock

class FakeTransactionRepository : TransactionRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> {
        return flowOf(
            listOf(
                Transaction(1, "Groceries", 50.0, Clock.System.now().toString().toLong()),
                Transaction(2, "Gas", 30.0, Clock.System.now().toString().toLong()),
                Transaction(3, "Dinner", 75.0, Clock.System.now().toString().toLong())
            )
        )
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        // No-op for preview
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        // No-op for preview
    }
}