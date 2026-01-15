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
                    value = entity.name,
                    description = entity.description,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun makeTransaction(transaction: Transaction): Long {
        val entity = TransactionEntity(
            name = transaction.value,
            description = transaction.description,
            createdAt = transaction.createdAt
        )
        return dao.addTransaction(entity)
    }

    override suspend fun deleteTransaction(id: Int): Int {
        return dao.deleteTransactionById(id)
    }
}
