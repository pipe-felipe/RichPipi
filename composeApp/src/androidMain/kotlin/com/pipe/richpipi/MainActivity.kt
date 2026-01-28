package com.pipe.richpipi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.pipe.richpipi.form.TransactionalViewModel
import com.pipe.richpipi.mainview.MainScreenViewModel
import data.local.database.DatabaseProvider
import data.repository.TransactionRepositoryImpl
import domain.usecase.DeleteTransactionUseCase
import domain.usecase.GetTransactions
import domain.usecase.MakeTransactionUseCase

class MainActivity : ComponentActivity() {

    private var mainScreenViewModel: MainScreenViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)


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
                onSignInSuccess = { viewModel ->
                    mainScreenViewModel = viewModel
                },
            )
        }
    }
}
