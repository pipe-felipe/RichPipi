package com.pipe.richpipi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pipe.richpipi.form.TransactionalViewModel
import com.pipe.richpipi.mainview.MainScreenViewModel
import data.local.database.DatabaseProvider
import data.remote.GoogleSignInHandler
import data.remote.initializeGoogleDriveService
import data.repository.TransactionRepositoryImpl
import domain.usecase.DeleteTransactionUseCase
import domain.usecase.GetTransactions
import domain.usecase.MakeTransactionUseCase
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var mainScreenViewModel: MainScreenViewModel? = null

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        lifecycleScope.launch {
            // Se a autenticação foi bem-sucedida, tenta criar a planilha novamente
            mainScreenViewModel?.onSignInSuccess()
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
                return TransactionalViewModel(
                    addItemUseCase,
                    getAllItemsUseCase,
                    deleteItemUseCase,
                ) as T
            }
        }

        val vm =
            ViewModelProvider(this, factory)[TransactionalViewModel::class.java]

        setContent {
            App(
                transactionalViewModel = vm,
                onSignInRequired = {
                    GoogleSignInHandler.getSignInIntent()?.let { intent ->
                        signInLauncher.launch(intent)
                    }
                },
                onSignInSuccess = { viewModel ->
                    mainScreenViewModel = viewModel
                },
            )
        }
    }
}
