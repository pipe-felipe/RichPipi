package com.pipe.richpipi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pipe.richpipi.data.local.getDatabase
import com.pipe.richpipi.domain.repository.FakeTransactionRepository
import com.pipe.richpipi.domain.usecase.GetAllTransactionsUseCase
import com.pipe.richpipi.domain.usecase.InsertTransactionUseCase
import com.pipe.richpipi.form.TransactionalViewModel
import com.pipe.richpipi.presentation.TransactionListViewModel
import com.pipe.richpipi.presentation.ViewModelFactory

class MainActivity : ComponentActivity() {
    private val database by lazy { getDatabase(this) }
    private val listViewModel: TransactionListViewModel by viewModels {
        ViewModelFactory(database)
    }
    private val transactionalViewModel: TransactionalViewModel by viewModels {
        ViewModelFactory(database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(listViewModel, transactionalViewModel)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val fakeRepository = FakeTransactionRepository()
    val getAllUseCase = GetAllTransactionsUseCase(fakeRepository)
    val listViewModel = TransactionListViewModel(getAllUseCase)
    val insertUseCase = InsertTransactionUseCase(fakeRepository)
    val transactionalViewModel = TransactionalViewModel(insertUseCase)
    App(listViewModel, transactionalViewModel)
}