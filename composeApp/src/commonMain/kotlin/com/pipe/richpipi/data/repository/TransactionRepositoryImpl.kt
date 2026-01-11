package com.pipe.richpipi.data.repository

import com.pipe.richpipi.data.local.TransactionDao
import com.pipe.richpipi.data.mapper.toDomain
import com.pipe.richpipi.data.mapper.fromDomain
import com.pipe.richpipi.domain.model.Transaction
import com.pipe.richpipi.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(private val dao: TransactionDao) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAll().map { transactions ->
            transactions.map { it.toDomain() }
        }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        dao.insert(transaction.fromDomain())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        dao.delete(transaction.fromDomain())
    }
}