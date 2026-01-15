package domain.repository

import domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(): Flow<List<Transaction>>
    suspend fun makeTransaction(transaction: Transaction): Long
    suspend fun deleteTransaction(id: Int): Int
}
