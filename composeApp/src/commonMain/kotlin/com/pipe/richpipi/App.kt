package com.pipe.richpipi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pipe.richpipi.form.TransactionalViewModel
import com.pipe.richpipi.form.TransactionalDialog
import com.pipe.richpipi.ui.theme.RichPipiTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(transactionalViewModel: TransactionalViewModel? = null) {
    var showDialog by remember { mutableStateOf(false) }
    val vm: TransactionalViewModel = transactionalViewModel ?: viewModel()

    // Collect items Flow from VM using collectAsState with initial value
    val itemsList by vm.items.collectAsState(initial = emptyList())

    RichPipiTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (itemsList.isEmpty()) {
                    Text("No items yet")
                } else {
                    itemsList.forEach { item ->
                        Text("${item.id}: ${item.name} - ${item.description}")
                    }
                }
            }
        }

        if (showDialog) {
            TransactionalDialog(
                viewModel = vm,
                onDismiss = { showDialog = false }
            )
        }
    }
}
