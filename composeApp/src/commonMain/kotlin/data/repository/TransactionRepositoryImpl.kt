package data.repository

import data.local.dao.TransactionDao
import data.local.entity.TransactionEntity
import domain.model.Transaction
import domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(private val dao: TransactionDao) : TransactionRepository {
    override fun getTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { list ->
            list.map { entity ->
                Transaction(
                    id = entity.id,
                    amountCents = entity.amountCents,
                    type = entity.type,
                    description = entity.description,
                    humanDate = entity.humanDate ?: "",
                    isRecurring = entity.isRecurring,
                    createdAt = entity.createdAt,
                )
            }
        }
    }

    override fun getTransactionsForMonth(
        monthStartMillis: Long,
        monthEndExclusiveMillis: Long,
    ): Flow<List<Transaction>> {
        return dao.getTransactionsForMonth(monthStartMillis, monthEndExclusiveMillis).map { list ->
            list.map { entity ->
                Transaction(
                    id = entity.id,
                    amountCents = entity.amountCents,
                    type = entity.type,
                    description = entity.description,
                    humanDate = entity.humanDate ?: "",
                    isRecurring = entity.isRecurring,
                    createdAt = entity.createdAt,
                )
            }
        }
    }

    override suspend fun makeTransaction(transaction: Transaction): Long {
        val entity = TransactionEntity(
            amountCents = transaction.amountCents,
            type = transaction.type,
            category = transaction.category,
            description = transaction.description,
            humanDate = transaction.humanDate,
            isRecurring = transaction.isRecurring,
            createdAt = transaction.createdAt,
        )
        return dao.addTransaction(entity)
    }

    override suspend fun deleteTransaction(id: Int): Int {
        return dao.deleteTransactionById(id)
    }
}
