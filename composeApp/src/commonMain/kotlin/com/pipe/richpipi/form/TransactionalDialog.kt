package com.pipe.richpipi.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.stringResource
import richpipi.composeapp.generated.resources.*

@Composable
private fun getCategoryString(category: Any): String {
    return when (category) {
        is ExpenseCategory -> when (category) {
            ExpenseCategory.TRANSPORT -> stringResource(Res.string.expense_category_transport)
            ExpenseCategory.GIFT -> stringResource(Res.string.expense_category_gift)
            ExpenseCategory.RECURRING -> stringResource(Res.string.expense_category_recurring)
            ExpenseCategory.FOOD -> stringResource(Res.string.expense_category_food)
            ExpenseCategory.STUFF -> stringResource(Res.string.expense_category_stuff)
            ExpenseCategory.MEDICINE -> stringResource(Res.string.expense_category_medicine)
            ExpenseCategory.CLOTHES -> stringResource(Res.string.expense_category_clothes)
        }
        is IncomeCategory -> when (category) {
            IncomeCategory.SALARY -> stringResource(Res.string.income_category_salary)
            IncomeCategory.GIFT -> stringResource(Res.string.income_category_gift)
            IncomeCategory.INVESTMENT -> stringResource(Res.string.income_category_investment)
            IncomeCategory.OTHER -> stringResource(Res.string.income_category_other)
        }
        else -> ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionalDialog(
    viewModel: TransactionalViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
        ) {
            Box {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (uiState.transactionType == TransactionType.EXPENSE),
                                onClick = { viewModel.onTransactionTypeChange(TransactionType.EXPENSE) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (uiState.transactionType == TransactionType.EXPENSE),
                            onClick = null // null recommended for accessibility with screenreaders
                        )
                        Text(
                            text = stringResource(Res.string.transaction_type_expense),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (uiState.transactionType == TransactionType.INCOME),
                                onClick = { viewModel.onTransactionTypeChange(TransactionType.INCOME) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (uiState.transactionType == TransactionType.INCOME),
                            onClick = null // null recommended for accessibility with screenreaders
                        )
                        Text(
                            text = stringResource(Res.string.transaction_type_income),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Categoria
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            value = if (uiState.transactionType == TransactionType.EXPENSE) getCategoryString(uiState.expenseCategory) else getCategoryString(uiState.incomeCategory),
                            onValueChange = {},
                            label = { Text(stringResource(Res.string.form_category_label)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            if (uiState.transactionType == TransactionType.EXPENSE) {
                                ExpenseCategory.values().forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(getCategoryString(selectionOption)) },
                                        onClick = {
                                            viewModel.onExpenseCategoryChange(selectionOption)
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            } else {
                                IncomeCategory.values().forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(getCategoryString(selectionOption)) },
                                        onClick = {
                                            viewModel.onIncomeCategoryChange(selectionOption)
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quantidade
                    OutlinedTextField(
                        value = uiState.quantity,
                        onValueChange = viewModel::onQuantityChange,
                        label = { Text(stringResource(Res.string.form_quantity_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true,
                        isError = uiState.isQuantityError
                    )

                    if (uiState.isQuantityError) {
                        Text(
                            text = "Número inválido",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Observações
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::onNotesChange,
                        label = { Text(stringResource(Res.string.form_notes_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.submit()
                            onDismiss()
                        },
                        modifier = Modifier.align(Alignment.End),
                        enabled = !uiState.isQuantityError
                    ) {
                        Text(stringResource(Res.string.form_submit_button))
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(
                            Res.string.form_close_button_description
                        )
                    )
                }
            }
        }
    }
}
