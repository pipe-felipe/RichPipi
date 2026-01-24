package com.pipe.richpipi

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pipe.richpipi.form.SaveDialog
import com.pipe.richpipi.form.TransactionalDialog
import com.pipe.richpipi.form.TransactionalViewModel
import com.pipe.richpipi.mainview.MainScreenContent
import com.pipe.richpipi.mainview.MainScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(transactionalViewModel: TransactionalViewModel? = null) {
    val showDialogState: MutableState<Boolean> = remember { mutableStateOf(false) }
    val showSaveDialogState: MutableState<Boolean> = remember { mutableStateOf(false) }

    val vm: TransactionalViewModel = transactionalViewModel ?: viewModel()
    val mainVm = remember {
        MainScreenViewModel(
            itemsSource = vm.items,
            onDeleteItem = { id -> vm.deleteItem(id) })
    }

    val itemsList by mainVm.items.collectAsState()
    val incomeText by mainVm.totalIncomeText.collectAsState()
    val expenseText by mainVm.totalExpenseText.collectAsState()
    val savingText by mainVm.totalSavingText.collectAsState()
    val currentMonthYear by mainVm.currentMonthYearText.collectAsState()
    val currentMonth by mainVm.currentMonth.collectAsState()
    val currentYear by mainVm.currentYear.collectAsState()

    MainScreenContent(
        itemsList = itemsList,
        totalIncomeText = incomeText,
        totalExpenseText = expenseText,
        totalSavingText = savingText,
        currentMonthYear = currentMonthYear,
        onPreviousMonth = { mainVm.goToPreviousMonth() },
        onNextMonth = { mainVm.goToNextMonth() },
        onCurrentMonthClick = { mainVm.goToCurrentMonth() },
        onAddButtonClick = { showDialogState.value = true },
        onSaveButtonClick = { showSaveDialogState.value = true },
        onDeleteItem = { id -> mainVm.delete(id) }
    )

    if (showDialogState.value) {
        TransactionalDialog(
            viewModel = vm,
            selectedMonth = currentMonth,
            selectedYear = currentYear,
            onDismiss = { showDialogState.value = false }
        )
    }

    if (showSaveDialogState.value) {
        SaveDialog(
            onDismiss = { showSaveDialogState.value = false },
            onSave = { onResult ->
                mainVm.createBackupFolder(onResult)
            }
        )
    }
}
