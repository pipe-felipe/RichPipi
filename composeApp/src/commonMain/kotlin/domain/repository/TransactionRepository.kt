package domain.repository

import domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(): Flow<List<Transaction>>

    fun getTransactionsForMonth(monthStartMillis: Long, monthEndExclusiveMillis: Long): Flow<List<Transaction>>

    suspend fun makeTransaction(transaction: Transaction): Long
    suspend fun deleteTransaction(id: Int): Int

    /**
     * Deletes all transactions from the database.
     * @return Number of transactions deleted
     */
    suspend fun deleteAllTransactions(): Int

    /**
     * Inserts multiple transactions at once.
     * @param transactions List of transactions to insert
     * @return List of inserted IDs
     */
    suspend fun insertTransactions(transactions: List<Transaction>): List<Long>
}
