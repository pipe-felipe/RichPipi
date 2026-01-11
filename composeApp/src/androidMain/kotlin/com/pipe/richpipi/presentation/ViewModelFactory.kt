package com.pipe.richpipi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pipe.richpipi.data.local.AppDatabase
import com.pipe.richpipi.data.repository.TransactionRepositoryImpl
import com.pipe.richpipi.domain.usecase.GetAllTransactionsUseCase
import com.pipe.richpipi.domain.usecase.InsertTransactionUseCase
import com.pipe.richpipi.form.TransactionalViewModel

class ViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = TransactionRepositoryImpl(database.transactionDao())

        return when {
            modelClass.isAssignableFrom(TransactionListViewModel::class.java) -> {
                val getAllTransactionsUseCase = GetAllTransactionsUseCase(repository)
                TransactionListViewModel(getAllTransactionsUseCase) as T
            }
            modelClass.isAssignableFrom(TransactionalViewModel::class.java) -> {
                val insertTransactionUseCase = InsertTransactionUseCase(repository)
                TransactionalViewModel(insertTransactionUseCase) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}