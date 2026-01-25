package com.pipe.richpipi.mainview

import com.pipe.richpipi.platform.currentMonthYear
import com.pipe.richpipi.platform.monthBoundsUtcMillis
import di.BackupModule
import domain.model.BackupResult
import domain.model.ImportResult
import domain.model.SpreadsheetFile
import domain.model.Transaction
import domain.usecase.AuthResult
import domain.usecase.ExportDataToSpreadsheetUseCase
import domain.usecase.ImportDataFromSpreadsheetUseCase
import domain.usecase.ListBackupsResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

/**
 * Lightweight view-model-like class for the main screen UI.
 * It is not an AndroidX ViewModel so it can be instantiated from common code easily.
 */
class MainScreenViewModel(
    itemsSource: Flow<List<Transaction>> = emptyFlow(),
    private val onDeleteItem: (Int) -> Unit = {},
    private val exportDataToSpreadsheetUseCase: ExportDataToSpreadsheetUseCase? = null,
    private val importDataFromSpreadsheetUseCase: ImportDataFromSpreadsheetUseCase? = null,
) {
    private val _items = MutableStateFlow<List<Transaction>>(emptyList())
    val items: StateFlow<List<Transaction>> = _items.asStateFlow()

    private val _totalIncomeText = MutableStateFlow("R$ 0.00")
    private val _totalExpenseText = MutableStateFlow("R$ 0.00")
    private val _totalSavingText = MutableStateFlow("R$ 0.00")

    val totalIncomeText: StateFlow<String> = _totalIncomeText.asStateFlow()
    val totalExpenseText: StateFlow<String> = _totalExpenseText.asStateFlow()
    val totalSavingText: StateFlow<String> = _totalSavingText.asStateFlow()

    private val _allItems = MutableStateFlow<List<Transaction>>(emptyList())

    private val initialMonthYear = currentMonthYear()
    private val _currentMonth = MutableStateFlow(initialMonthYear.first)
    private val _currentYear = MutableStateFlow(initialMonthYear.second)
    val currentMonth: StateFlow<Int> = _currentMonth.asStateFlow()
    val currentYear: StateFlow<Int> = _currentYear.asStateFlow()

    private val _currentMonthYearText =
        MutableStateFlow(
            formatMonthYear(initialMonthYear.first, initialMonthYear.second),
        )
    val currentMonthYearText: StateFlow<String> =
        _currentMonthYearText.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    fun authenticate(onSignInRequired: () -> Unit) {
        scope.launch {
            try {
                when (val result = BackupModule.authenticateUseCase.execute()) {
                    is AuthResult.Success -> {
                        _userName.value = result.userName
                    }

                    is AuthResult.SignInRequired -> {
                        onSignInRequired()
                    }

                    is AuthResult.Error -> {
                        // Optionally handle error
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun refreshUserName() {
        scope.launch {
            try {
                val name = BackupModule.authenticateUseCase.getCurrentUserName()
                _userName.value = name
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    init {
        scope.launch {
            try {
                itemsSource.collect { list ->
                    _allItems.value = list
                }
            } catch (_: Throwable) {
                // ignore
            }
        }

        scope.launch {
            combine(
                _allItems,
                _currentMonth,
                _currentYear
            ) { list, month, year ->
                val (start, endExclusive) = monthBoundsUtcMillis(
                    month = month,
                    year = year
                )
                list.filter { tx ->
                    // Recurring items should only be considered once the month reaches their start date
                    // (createdAt is stored as epoch millis).
                    if (tx.isRecurring) {
                        tx.createdAt < endExclusive
                    } else {
                        tx.createdAt in start..<endExclusive
                    }
                }
            }.collect { filtered ->
                _items.value = filtered

                // Month-scoped totals
                val (inc, exp) = computeTotals(filtered)
                _totalIncomeText.value = "R$ ${formatTwoDecimals(inc)}"
                _totalExpenseText.value = "R$ ${formatTwoDecimals(exp)}"

                // Accumulated saving up to the end of the selected month:
                // - Non-recurring: count if tx.createdAt < selectedMonthEndExclusive
                // - Recurring: only counts once the month reaches its start month, so also require tx.createdAt < selectedMonthEndExclusive
                val (_, selectedMonthEndExclusive) =
                    monthBoundsUtcMillis(
                        month = _currentMonth.value,
                        year = _currentYear.value
                    )

                val accumulatedItems = _allItems.value.filter { tx ->
                    tx.createdAt < selectedMonthEndExclusive
                }

                val (allInc, allExp) = computeTotals(accumulatedItems)
                _totalSavingText.value =
                    "R$ ${formatTwoDecimals(allInc - allExp)}"
            }
        }
    }

    fun delete(id: Int) = onDeleteItem(id)

    fun goToPreviousMonth() {
        val month = _currentMonth.value
        val year = _currentYear.value

        if (month == 1) {
            _currentMonth.value = 12
            _currentYear.value = year - 1
        } else {
            _currentMonth.value = month - 1
        }
        _currentMonthYearText.value =
            formatMonthYear(_currentMonth.value, _currentYear.value)
    }

    fun goToNextMonth() {
        val month = _currentMonth.value
        val year = _currentYear.value

        if (month == 12) {
            _currentMonth.value = 1
            _currentYear.value = year + 1
        } else {
            _currentMonth.value = month + 1
        }
        _currentMonthYearText.value =
            formatMonthYear(_currentMonth.value, _currentYear.value)
    }

    fun goToCurrentMonth() {
        val (month, year) = currentMonthYear()
        _currentMonth.value = month
        _currentYear.value = year
        _currentMonthYearText.value = formatMonthYear(month, year)
    }

    private var onResultCallback: ((BackupResult) -> Unit)? = null

    private val _backupResult = MutableStateFlow<BackupResult?>(null)
    val backupResult: StateFlow<BackupResult?> = _backupResult.asStateFlow()

    fun clearBackupResult() {
        _backupResult.value = null
    }

    fun backupToDrive(
        onResult: (BackupResult) -> Unit,
        onSignInRequired: () -> Unit
    ) {
        onResultCallback = onResult
        _backupResult.value = null
        scope.launch {
            try {
                when (val result = createSpreadsheet()) {
                    is BackupResult.SignInRequired -> {
                        _backupResult.value = result
                        onResult(result) // Notifica o dialog para mostrar a mensagem
                        onSignInRequired() // Abre a tela de login
                    }

                    else -> {
                        _backupResult.value = result
                        onResult(result)
                    }
                }
            } catch (e: Exception) {
                val error = BackupResult.Error("Falha ao criar backup", e)
                _backupResult.value = error
                onResult(error)
            }
        }
    }

    fun onSignInSuccess() {
        scope.launch {
            // Refresh user name after successful sign-in
            refreshUserName()

            try {
                val result = createSpreadsheet()
                _backupResult.value = result
                onResultCallback?.invoke(result)
            } catch (e: Exception) {
                val error =
                    BackupResult.Error("Falha ao criar backup após o login", e)
                _backupResult.value = error
                onResultCallback?.invoke(error)
            }
        }
    }

    private suspend fun createSpreadsheet(): BackupResult {
        // If we have an export use case, use it to export data with content
        // Otherwise, fall back to the simple spreadsheet creation
        return exportDataToSpreadsheetUseCase?.execute(
            Clock.System.now().toEpochMilliseconds(),
        ) ?: BackupModule.createSpreadSheetUseCase.execute(
            Clock.System.now().toEpochMilliseconds(),
        )
    }

    // Restore functionality
    private val _availableBackups =
        MutableStateFlow<List<SpreadsheetFile>>(emptyList())
    val availableBackups: StateFlow<List<SpreadsheetFile>> =
        _availableBackups.asStateFlow()

    private val _restoreResult = MutableStateFlow<ImportResult?>(null)
    val restoreResult: StateFlow<ImportResult?> = _restoreResult.asStateFlow()

    private val _isLoadingBackups = MutableStateFlow(false)
    val isLoadingBackups: StateFlow<Boolean> = _isLoadingBackups.asStateFlow()

    fun clearRestoreResult() {
        _restoreResult.value = null
    }

    private var onRestoreSignInRequired: (() -> Unit)? = null

    fun loadAvailableBackups(onSignInRequired: () -> Unit) {
        onRestoreSignInRequired = onSignInRequired
        scope.launch {
            _isLoadingBackups.value = true
            _restoreResult.value = null
            _availableBackups.value = emptyList()
            try {
                when (val result =
                    BackupModule.listBackupSpreadsheetsUseCase.execute()) {
                    is ListBackupsResult.Success -> {
                        _availableBackups.value = result.spreadsheets
                        if (result.spreadsheets.isEmpty()) {
                            _restoreResult.value = ImportResult.NoBackupsFound
                        }
                    }

                    is ListBackupsResult.SignInRequired -> {
                        _restoreResult.value = ImportResult.SignInRequired
                        onSignInRequired()
                    }

                    is ListBackupsResult.Error -> {
                        _restoreResult.value =
                            ImportResult.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _restoreResult.value =
                    ImportResult.Error("Falha ao carregar backups", e)
            } finally {
                _isLoadingBackups.value = false
            }
        }
    }

    fun restoreFromBackup(spreadsheetId: String, onSignInRequired: () -> Unit) {
        scope.launch {
            _restoreResult.value = null
            try {
                val result =
                    importDataFromSpreadsheetUseCase?.execute(spreadsheetId)
                        ?: ImportResult.Error("Funcionalidade de importação não disponível")

                _restoreResult.value = result

                if (result is ImportResult.SignInRequired) {
                    onSignInRequired()
                }
            } catch (e: Exception) {
                _restoreResult.value =
                    ImportResult.Error("Falha ao restaurar backup", e)
            }
        }
    }

    private fun formatMonthYear(month: Int, year: Int): String {
        val monthNames = listOf(
            "Janeiro",
            "Fevereiro",
            "Março",
            "Abril",
            "Maio",
            "Junho",
            "Julho",
            "Agosto",
            "Setembro",
            "Outubro",
            "Novembro",
            "Dezembro",
        )
        return "${monthNames[month - 1]} $year"
    }

    internal fun computeTotalsForTest(items: List<Transaction>): Pair<Double, Double> =
        computeTotals(items)
}
