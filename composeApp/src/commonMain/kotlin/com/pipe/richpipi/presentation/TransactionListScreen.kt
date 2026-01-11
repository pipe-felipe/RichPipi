package com.pipe.richpipi.presentation

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun TransactionListScreen(viewModel: TransactionListViewModel) {
    val state by viewModel.state.collectAsState()

    LazyColumn {
        items(state.transactions) { transaction ->
            Text(text = transaction.name)
        }
    }
}