package com.pipe.richpipi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pipe.richpipi.domain.repository.FakeTransactionRepository
import com.pipe.richpipi.domain.usecase.GetAllTransactionsUseCase
import com.pipe.richpipi.domain.usecase.InsertTransactionUseCase
import com.pipe.richpipi.form.TransactionalDialog
import com.pipe.richpipi.form.TransactionalViewModel
import com.pipe.richpipi.presentation.TransactionListScreen
import com.pipe.richpipi.presentation.TransactionListViewModel
import com.pipe.richpipi.ui.theme.RichPipiTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    listViewModel: TransactionListViewModel,
    transactionalViewModel: TransactionalViewModel
) {
    var showDialog by remember { mutableStateOf(false) }

    RichPipiTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) {
            TransactionListScreen(listViewModel)
        }

        if (showDialog) {
            TransactionalDialog(
                viewModel = transactionalViewModel,
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    val fakeRepository = FakeTransactionRepository()
    val getAllUseCase = GetAllTransactionsUseCase(fakeRepository)
    val listViewModel = TransactionListViewModel(getAllUseCase)
    val insertUseCase = InsertTransactionUseCase(fakeRepository)
    val transactionalViewModel = TransactionalViewModel(insertUseCase)
    App(listViewModel, transactionalViewModel)
}
