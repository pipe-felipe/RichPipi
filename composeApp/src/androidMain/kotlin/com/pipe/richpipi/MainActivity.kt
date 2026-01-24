package com.pipe.richpipi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.pipe.richpipi.form.TransactionalViewModel
import data.local.database.DatabaseProvider
import data.remote.initializeGoogleDriveService
import data.repository.TransactionRepositoryImpl
import domain.usecase.MakeTransactionUseCase
import domain.usecase.GetTransactions
import domain.usecase.DeleteTransactionUseCase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize Google Drive service
        initializeGoogleDriveService(this)

        val db = DatabaseProvider.provideDatabase(this)
        val repo = TransactionRepositoryImpl(db.transactionDao())
        val addItemUseCase = MakeTransactionUseCase(repo)
        val getAllItemsUseCase = GetTransactions(repo)
        val deleteItemUseCase = DeleteTransactionUseCase(repo)

        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return TransactionalViewModel(addItemUseCase, getAllItemsUseCase, deleteItemUseCase) as T
            }
        }

        // Obtain ViewModel instance scoped to this Activity using the factory
        val vm = ViewModelProvider(this, factory).get(TransactionalViewModel::class.java)

        setContent {
            App(transactionalViewModel = vm)
        }

    }
}
