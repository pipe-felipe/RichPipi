package com.pipe.richpipi

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pipe.richpipi.form.TransactionalDialog
import com.pipe.richpipi.form.TransactionalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(transactionalViewModel: TransactionalViewModel? = null) {
    var showDialog by remember { mutableStateOf(false) }
    val vm: TransactionalViewModel = transactionalViewModel ?: viewModel()

    val itemsList by vm.items.collectAsState(initial = emptyList())

    MainScreenContent(
        itemsList = itemsList,
        onAddButtonClick = { showDialog = true }
    )

    if (showDialog) {
        TransactionalDialog(
            viewModel = vm,
            onDismiss = { showDialog = false }
        )
    }
}

