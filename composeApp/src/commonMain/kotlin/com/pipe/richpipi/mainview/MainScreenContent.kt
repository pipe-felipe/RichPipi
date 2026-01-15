package com.pipe.richpipi.mainview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pipe.richpipi.ui.theme.RichPipiTheme
import domain.model.Transaction


@Composable
fun MainScreenContent(
    itemsList: List<Transaction>,
    totalIncomeText: String,
    totalExpenseText: String,
    currentMonthYear: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddButtonClick: () -> Unit,
    onDeleteItem: (Int) -> Unit
) {
    RichPipiTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                MainTopBar(
                    totalIncomeText = totalIncomeText,
                    totalExpenseText = totalExpenseText,
                    currentMonthYear = currentMonthYear,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAddButtonClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { innerPadding ->
            if (itemsList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    Text(
                        text = "Sem transações para mostrar",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(itemsList) { item ->
                        ItemRow(item = item, onDelete = { onDeleteItem(item.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemRow(item: Transaction, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.type.name} - ${formatMoneyFromCents(item.amountCents)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                item.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Text(
                text = item.id.toString(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                )
            }
        }
    }
}
