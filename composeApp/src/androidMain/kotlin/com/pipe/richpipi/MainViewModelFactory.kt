package com.pipe.richpipi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pipe.richpipi.form.TransactionalViewModel
import data.local.database.DatabaseProvider
import data.repository.TransactionRepositoryImpl
import domain.usecase.DeleteTransactionUseCase
import domain.usecase.GetTransactions
import domain.usecase.MakeTransactionUseCase

class MainViewModelFactory(private val dbProvider: DatabaseProvider) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = dbProvider.provideDatabase(appContext())
        val repo = TransactionRepositoryImpl(db.transactionDao())
        val addItemUseCase = MakeTransactionUseCase(repo)
        val getAllItemsUseCase = GetTransactions(repo)
        val deleteItemUseCase = DeleteTransactionUseCase(repo)
        return TransactionalViewModel(addItemUseCase, getAllItemsUseCase, deleteItemUseCase) as T
    }

    // Since this factory is in Activity scope, we need a way to obtain a Context; we'll provide a helper
    private fun appContext(): android.content.Context {
        // This is a small workaround: DatabaseProvider.provideDatabase requires Context.
        // In MainActivity we will not use this factory's default; instead we'll create the factory with a context-aware provider.
        throw IllegalStateException("Use the constructor that supplies a Context-aware provider from Activity")
    }
}
