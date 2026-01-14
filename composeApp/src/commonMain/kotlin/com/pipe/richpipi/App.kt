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
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pipe.richpipi.form.TransactionalViewModel
import com.pipe.richpipi.form.TransactionalDialog
import com.pipe.richpipi.ui.theme.RichPipiTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    var showDialog by remember { mutableStateOf(false) }
    val transactionalViewModel: TransactionalViewModel = viewModel()
    val items by transactionalViewModel.items.collectAsState()

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
                if (items.isEmpty()) {
                    Text("No items yet")
                } else {
                    items.forEach { item ->
                        Text("${item.id}: ${item.name} - ${item.description}")
                    }
                }
            }
        }

        if (showDialog) {
            TransactionalDialog(
                viewModel = transactionalViewModel,
                onDismiss = { showDialog = false }
            )
        }
    }
}
