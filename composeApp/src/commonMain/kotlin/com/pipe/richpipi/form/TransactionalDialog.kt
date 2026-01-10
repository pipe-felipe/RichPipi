package com.pipe.richpipi.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.stringResource
import richpipi.composeapp.generated.resources.Res
import richpipi.composeapp.generated.resources.expense_category_clothes
import richpipi.composeapp.generated.resources.expense_category_food
import richpipi.composeapp.generated.resources.expense_category_gift
import richpipi.composeapp.generated.resources.expense_category_medicine
import richpipi.composeapp.generated.resources.expense_category_recurring
import richpipi.composeapp.generated.resources.expense_category_stuff
import richpipi.composeapp.generated.resources.expense_category_transport
import richpipi.composeapp.generated.resources.form_category_label
import richpipi.composeapp.generated.resources.form_close_button_description
import richpipi.composeapp.generated.resources.form_notes_label
import richpipi.composeapp.generated.resources.form_quantity_label
import richpipi.composeapp.generated.resources.form_submit_button
import richpipi.composeapp.generated.resources.income_category_gift
import richpipi.composeapp.generated.resources.income_category_investment
import richpipi.composeapp.generated.resources.income_category_other
import richpipi.composeapp.generated.resources.income_category_salary
import richpipi.composeapp.generated.resources.label_invalid_number
import richpipi.composeapp.generated.resources.transaction_type_expense
import richpipi.composeapp.generated.resources.transaction_type_income

@Composable
private fun mapCategory(category: Any): String {
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
    viewModel: TransactionalViewModel, onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
        ) {
            Box {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().offset(x = -(8).dp, y = -(5).dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val radioOptions = listOf(TransactionType.EXPENSE, TransactionType.INCOME)
                        radioOptions.forEach { option ->
                            Row(
                                Modifier.selectable(
                                    selected = (uiState.transactionType == option),
                                    onClick = { viewModel.onTransactionTypeChange(option) },
                                    role = Role.RadioButton
                                ).padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (uiState.transactionType == option), onClick = null
                                )
                                Text(
                                    text = if (option == TransactionType.EXPENSE) stringResource(
                                        Res.string.transaction_type_expense
                                    ) else stringResource(
                                        Res.string.transaction_type_income
                                    ),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                    ) {
                        ExposedDropdownMenuBox(
                            modifier = Modifier.weight(1f),
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                readOnly = true,
                                value = if (uiState.transactionType == TransactionType.EXPENSE) mapCategory(
                                    uiState.expenseCategory
                                ) else mapCategory(uiState.incomeCategory),
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
                                    ExpenseCategory.entries.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text(mapCategory(selectionOption)) },
                                            onClick = {
                                                viewModel.onExpenseCategoryChange(selectionOption)
                                                expanded = false
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                        )
                                    }
                                } else {
                                    IncomeCategory.entries.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text(mapCategory(selectionOption)) },
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

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = uiState.quantity,
                            onValueChange = viewModel::onQuantityChange,
                            label = { Text(stringResource(Res.string.form_quantity_label)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            singleLine = true,
                            isError = uiState.isQuantityError,
                            supportingText = {
                                if (uiState.isQuantityError) {
                                    Text(
                                        text = stringResource(Res.string.label_invalid_number)
                                    )
                                }
                            }
                        )
                    }

                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::onNotesChange,
                        label = { Text(stringResource(Res.string.form_notes_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = (3).dp, y = -(2.5).dp)
                ) {
                    Icon(
                        Icons.Default.Close, contentDescription = stringResource(
                            Res.string.form_close_button_description
                        )
                    )
                }
            }
        }
    }
}