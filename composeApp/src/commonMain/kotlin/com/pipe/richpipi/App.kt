package com.pipe.richpipi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pipe.richpipi.form.TransactionalViewModel
import com.pipe.richpipi.form.TransactionalDialog
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    var showDialog by remember { mutableStateOf(false) }
    // Get a ViewModel instance that is tied to the lifecycle of this screen
    val transactionalViewModel: TransactionalViewModel = viewModel()

    MaterialTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) {
            // O conteúdo principal da tela pode ser adicionado aqui
        }

        if (showDialog) {
            TransactionalDialog(
                viewModel = transactionalViewModel,
                onDismiss = { showDialog = false }
            )
        }
    }
}
