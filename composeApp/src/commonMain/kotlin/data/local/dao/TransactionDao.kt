package data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions
        WHERE isRecurring = 1
           OR (createdAt >= :monthStartMillis AND createdAt < :monthEndExclusiveMillis)
        ORDER BY createdAt DESC
        """,
    )
    fun getTransactionsForMonth(
        monthStartMillis: Long,
        monthEndExclusiveMillis: Long,
    ): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Int): TransactionEntity?

    @Insert
    suspend fun addTransaction(item: TransactionEntity): Long

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Int): Int
}
