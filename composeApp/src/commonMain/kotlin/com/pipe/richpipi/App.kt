package com.pipe.richpipi

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pipe.richpipi.form.RestoreDialog
import com.pipe.richpipi.form.SaveDialog
import com.pipe.richpipi.form.TransactionalDialog
import com.pipe.richpipi.form.TransactionalViewModel
import com.pipe.richpipi.mainview.MainScreenContent
import com.pipe.richpipi.mainview.MainScreenViewModel
import domain.usecase.ExportDataToSpreadsheetUseCase
import domain.usecase.ImportDataFromSpreadsheetUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    transactionalViewModel: TransactionalViewModel? = null,
    exportDataToSpreadsheetUseCase: ExportDataToSpreadsheetUseCase? = null,
    importDataFromSpreadsheetUseCase: ImportDataFromSpreadsheetUseCase? = null,
    onSignInRequired: () -> Unit = {},
    onSignInSuccess: ((MainScreenViewModel) -> Unit)? = null,
) {
    val showDialogState: MutableState<Boolean> = remember { mutableStateOf(false) }
    val showSaveDialogState: MutableState<Boolean> = remember { mutableStateOf(false) }
    val showRestoreDialogState: MutableState<Boolean> = remember { mutableStateOf(false) }

    val vm: TransactionalViewModel = transactionalViewModel ?: viewModel()
    val mainVm = remember {
        MainScreenViewModel(
            itemsSource = vm.items,
            onDeleteItem = { id -> vm.deleteItem(id) },
            exportDataToSpreadsheetUseCase = exportDataToSpreadsheetUseCase,
            importDataFromSpreadsheetUseCase = importDataFromSpreadsheetUseCase,
        )
    }

    // Expor o mainVm através do callback onSignInSuccess quando fornecido
    onSignInSuccess?.invoke(mainVm)

    val itemsList by mainVm.items.collectAsState()
    val incomeText by mainVm.totalIncomeText.collectAsState()
    val expenseText by mainVm.totalExpenseText.collectAsState()
    val savingText by mainVm.totalSavingText.collectAsState()
    val currentMonthYear by mainVm.currentMonthYearText.collectAsState()
    val currentMonth by mainVm.currentMonth.collectAsState()
    val currentYear by mainVm.currentYear.collectAsState()
    val backupResult by mainVm.backupResult.collectAsState()
    val availableBackups by mainVm.availableBackups.collectAsState()
    val restoreResult by mainVm.restoreResult.collectAsState()
    val isLoadingBackups by mainVm.isLoadingBackups.collectAsState()
    val authStatus by mainVm.authStatus.collectAsState()

    // Try to get the user name on startup
    androidx.compose.runtime.LaunchedEffect(Unit) {
        mainVm.refreshUserName()
    }

    MainScreenContent(
        itemsList = itemsList,
        totalIncomeText = incomeText,
        totalExpenseText = expenseText,
        totalSavingText = savingText,
        currentMonthYear = currentMonthYear,
        authStatus = authStatus,
        onPreviousMonth = { mainVm.goToPreviousMonth() },
        onNextMonth = { mainVm.goToNextMonth() },
        onCurrentMonthClick = { mainVm.goToCurrentMonth() },
        onAddButtonClick = { showDialogState.value = true },
        onSaveButtonClick = { showSaveDialogState.value = true },
        onRestoreButtonClick = { showRestoreDialogState.value = true },
        onLoginButtonClick = { mainVm.authenticate(onSignInRequired) },
        onDeleteItem = { id -> mainVm.delete(id) },
    )

    if (showDialogState.value) {
        TransactionalDialog(
            viewModel = vm,
            selectedMonth = currentMonth,
            selectedYear = currentYear,
            onDismiss = { showDialogState.value = false },
        )
    }

    if (showSaveDialogState.value) {
        SaveDialog(
            onDismiss = { showSaveDialogState.value = false },
            onSave = {
                mainVm.backupToDrive(
                    onResult = { /* resultado será observado via backupResult */ },
                    onSignInRequired = onSignInRequired,
                )
            },
            backupResult = backupResult,
            onClearResult = { mainVm.clearBackupResult() },
        )
    }

    if (showRestoreDialogState.value) {
        RestoreDialog(
            onDismiss = { showRestoreDialogState.value = false },
            onRestore = { spreadsheetId ->
                mainVm.restoreFromBackup(
                    spreadsheetId = spreadsheetId,
                    onSignInRequired = onSignInRequired,
                )
            },
            onLoadBackups = { mainVm.loadAvailableBackups(onSignInRequired) },
            availableBackups = availableBackups,
            restoreResult = restoreResult,
            isLoading = isLoadingBackups,
            onClearResult = { mainVm.clearRestoreResult() },
        )
    }
}
