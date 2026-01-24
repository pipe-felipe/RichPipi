package com.pipe.richpipi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pipe.richpipi.form.TransactionalViewModel
import data.local.database.DatabaseProvider
import data.remote.GoogleSignInHandler
import data.remote.initializeGoogleDriveService
import data.repository.TransactionRepositoryImpl
import domain.usecase.DeleteTransactionUseCase
import domain.usecase.GetTransactions
import domain.usecase.MakeTransactionUseCase
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        lifecycleScope.launch {
            GoogleSignInHandler.handleSignInResult(result.data)
        }
    }

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
            App(
                transactionalViewModel = vm,
                onSignInRequired = {
                    GoogleSignInHandler.getSignInIntent()?.let { intent ->
                        signInLauncher.launch(intent)
                    }
                },
            )
        }
    }
}
