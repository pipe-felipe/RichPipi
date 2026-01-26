package com.pipe.richpipi.mainview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pipe.richpipi.ui.theme.expenseBackground
import com.pipe.richpipi.ui.theme.incomeBackground
import domain.model.Transaction
import domain.model.TransactionType
import org.jetbrains.compose.resources.stringResource
import richpipi.composeapp.generated.resources.Res
import richpipi.composeapp.generated.resources.no_transaction

@Composable
fun MainScreenContent(
    itemsList: List<Transaction>,
    totalIncomeText: String,
    totalExpenseText: String,
    totalSavingText: String,
    currentMonthYear: String,
    authStatus: MainScreenViewModel.AuthStatus,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onCurrentMonthClick: () -> Unit,
    onAddButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
    onRestoreButtonClick: () -> Unit,
    onLoginButtonClick: () -> Unit,
    onLoginRequiredClick: () -> Unit,
    onDeleteItem: (Int) -> Unit,
) {
    val isAuthenticated = authStatus is MainScreenViewModel.AuthStatus.Authenticated

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                MainTopBar(
                    totalIncomeText = totalIncomeText,
                    totalExpenseText = totalExpenseText,
                    totalSavingText = totalSavingText,
                    currentMonthYear = currentMonthYear,
                    authStatus = authStatus,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth,
                    onCurrentMonthClick = onCurrentMonthClick,
                )
            },
        ) { innerPadding ->
            if (itemsList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    Text(
                        text = stringResource(Res.string.no_transaction),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 88.dp,
                    ),
                ) {
                    items(itemsList) { item ->
                        ItemRow(
                            item = item,
                            onDelete = { onDeleteItem(item.id) },
                        )
                    }
                }
            }
        }

        MainBottomBar(
            onAddButtonClick = onAddButtonClick,
            onSaveButtonClick = if (isAuthenticated) onSaveButtonClick else onLoginRequiredClick,
            onRestoreButtonClick = if (isAuthenticated) onRestoreButtonClick else onLoginRequiredClick,
            onLoginButtonClick = onLoginButtonClick,
            isAuthenticated = isAuthenticated,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun ItemRow(
    item: Transaction,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.type == TransactionType.INCOME) {
                MaterialTheme.colorScheme.incomeBackground
            } else {
                MaterialTheme.colorScheme.expenseBackground
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                item.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.humanDate,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Text(
                text = if (item.type == TransactionType.INCOME) {
                    "R$${formatMoneyFromCents(item.amountCents)}"
                } else {
                    "-R$${formatMoneyFromCents(item.amountCents)}"
                },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .offset(x = 12.dp),
            )
            IconButton(
                onClick = onDelete,
                modifier = Modifier.offset(x = 12.dp),
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                )
            }
        }
    }
}
