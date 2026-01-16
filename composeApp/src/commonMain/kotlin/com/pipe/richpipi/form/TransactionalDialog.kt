package com.pipe.richpipi.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.window.DialogProperties
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
import richpipi.composeapp.generated.resources.form_recurring_label
import richpipi.composeapp.generated.resources.form_submit_button
import richpipi.composeapp.generated.resources.income_category_gift
import richpipi.composeapp.generated.resources.income_category_investment
import richpipi.composeapp.generated.resources.income_category_other
import richpipi.composeapp.generated.resources.income_category_salary
import richpipi.composeapp.generated.resources.label_invalid_number
import richpipi.composeapp.generated.resources.transaction_type_expense
import richpipi.composeapp.generated.resources.transaction_type_income
import domain.model.TransactionType as DomainTransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionalDialog(
    viewModel: TransactionalViewModel,
    selectedMonth: Int,
    selectedYear: Int,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val hasValidQuantity = uiState.quantity.isNotBlank() && !uiState.isQuantityError
    val hasCategory =
        if (uiState.transactionType == DomainTransactionType.EXPENSE)
            uiState.expenseCategory != null
        else uiState.incomeCategory != null
    val isSubmitEnabled = hasValidQuantity && hasCategory

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints {
            val dialogPadding = if (maxWidth < 400.dp) 4.dp else 15.dp

            Surface(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(dialogPadding)
            ) {
                Box {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TransactionTypeSelector(
                            selectedType = uiState.transactionType,
                            onTypeSelected = viewModel::onTransactionTypeChange
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CategoryAndQuantityInput(
                            uiState = uiState,
                            onExpenseCategoryChange = viewModel::onExpenseCategoryChange,
                            onIncomeCategoryChange = viewModel::onIncomeCategoryChange,
                            onQuantityChange = viewModel::onQuantityChange,
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                        )

                        NotesInput(
                            notes = uiState.notes,
                            onNotesChange = viewModel::onNotesChange
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        ActionsBar(
                            isRecurring = uiState.isRecurring,
                            onRecurringChange = viewModel::onRecurringChange,
                            isSubmitEnabled = isSubmitEnabled,
                            onSubmit = {
                                viewModel.submit(selectedMonth, selectedYear)
                                onDismiss()
                            }
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                            .offset(x = (3).dp, y = -(2.5).dp)
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
}

@Composable
private fun TransactionTypeSelector(
    selectedType: DomainTransactionType,
    onTypeSelected: (DomainTransactionType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val radioOptions = listOf(DomainTransactionType.EXPENSE, DomainTransactionType.INCOME)
        radioOptions.forEach { option ->
            Row(
                Modifier.selectable(
                    selected = (selectedType == option),
                    onClick = { onTypeSelected(option) },
                    role = Role.RadioButton
                ).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = (selectedType == option), onClick = null)
                Text(
                    text = if (option == DomainTransactionType.EXPENSE) stringResource(Res.string.transaction_type_expense)
                    else stringResource(Res.string.transaction_type_income),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryAndQuantityInput(
    uiState: FormUiState,
    onExpenseCategoryChange: (ExpenseCategory) -> Unit,
    onIncomeCategoryChange: (IncomeCategory) -> Unit,
    onQuantityChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.weight(1f),
            expanded = expanded,
            onExpandedChange = onExpandedChange,
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                readOnly = true,
                value = if (uiState.transactionType == DomainTransactionType.EXPENSE) mapCategory(
                    uiState.expenseCategory
                )
                else mapCategory(uiState.incomeCategory),
                onValueChange = {},
                label = { Text(stringResource(Res.string.form_category_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
            ) {
                if (uiState.transactionType == DomainTransactionType.EXPENSE) {
                    ExpenseCategory.entries.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(mapCategory(selectionOption)) },
                            onClick = {
                                onExpenseCategoryChange(selectionOption)
                                onExpandedChange(false)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                } else {
                    IncomeCategory.entries.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(mapCategory(selectionOption)) },
                            onClick = {
                                onIncomeCategoryChange(selectionOption)
                                onExpandedChange(false)
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
            onValueChange = onQuantityChange,
            label = { Text(stringResource(Res.string.form_quantity_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = uiState.isQuantityError,
            supportingText = {
                if (uiState.isQuantityError) {
                    Text(text = stringResource(Res.string.label_invalid_number))
                }
            }
        )
    }
}

@Composable
private fun NotesInput(notes: String, onNotesChange: (String) -> Unit) {
    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text(stringResource(Res.string.form_notes_label)) },
        modifier = Modifier.fillMaxWidth().offset(y = -(8).dp)
    )
}

@Composable
private fun ActionsBar(
    isRecurring: Boolean,
    onRecurringChange: (Boolean) -> Unit,
    isSubmitEnabled: Boolean,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.form_recurring_label))
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isRecurring,
                onCheckedChange = onRecurringChange
            )
        }
        Button(
            onClick = onSubmit,
            enabled = isSubmitEnabled
        ) {
            Text(stringResource(Res.string.form_submit_button))
        }
    }
}

@Composable
private fun mapCategory(category: Any?): String {
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
