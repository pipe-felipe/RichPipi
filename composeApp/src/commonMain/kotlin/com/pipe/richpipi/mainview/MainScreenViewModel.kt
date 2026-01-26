package com.pipe.richpipi.mainview

import com.pipe.richpipi.platform.currentMonthYear
import di.BackupModule
import domain.model.BackupResult
import domain.model.ImportResult
import domain.model.SpreadsheetFile
import domain.model.Transaction
import domain.usecase.AuthResult
import domain.usecase.ExportDataToSpreadsheetUseCase
import domain.usecase.IAuthenticateUseCase
import domain.usecase.ImportDataFromSpreadsheetUseCase
import domain.usecase.ListBackupsResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlin.time.Clock

/**
 * Lightweight view-model-like class for the main screen UI.
 * It is not an AndroidX ViewModel so it can be instantiated from common code easily.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModel(
    private val itemsSource: Flow<List<Transaction>> = emptyFlow(),
    private val onDeleteItem: (Int) -> Unit = {},
    private val exportDataToSpreadsheetUseCase: ExportDataToSpreadsheetUseCase? = null,
    private val importDataFromSpreadsheetUseCase: ImportDataFromSpreadsheetUseCase? = null,
    private val authenticateUseCase: IAuthenticateUseCase = BackupModule.authenticateUseCase,
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

    // Trigger to force refresh of items from the source
    private val _refreshTrigger = MutableStateFlow(0)

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

    sealed class AuthStatus {
        object NotAuthenticated : AuthStatus()
        data class Authenticated(val userName: String) : AuthStatus()
        object Error : AuthStatus()
    }

    private val _authStatus =
        MutableStateFlow<AuthStatus>(AuthStatus.NotAuthenticated)
    val authStatus: StateFlow<AuthStatus> = _authStatus

    fun authenticate(onSignInRequired: () -> Unit) {
        scope.launch {
            try {
                when (val result = authenticateUseCase.execute()) {
                    is AuthResult.Success -> {
                        _userName.value = result.userName
                        _authStatus.value = AuthStatus.Authenticated(
                            result.userName ?: "Desconhecido",
                        )
                    }

                    is AuthResult.SignInRequired -> {
                        _authStatus.value = AuthStatus.NotAuthenticated
                        onSignInRequired()
                    }

                    is AuthResult.Error -> {
                        _authStatus.value = AuthStatus.Error
                    }
                }
            } catch (e: Exception) {
                _authStatus.value = AuthStatus.Error
            }
        }
    }

    fun refreshUserName() {
        scope.launch {
            try {
                val name = authenticateUseCase.getCurrentUserName()
                _userName.value = name
                if (name.isNullOrBlank()) {
                    _authStatus.value = AuthStatus.NotAuthenticated
                } else {
                    _authStatus.value = AuthStatus.Authenticated(name)
                }
            } catch (e: Exception) {
                _authStatus.value = AuthStatus.Error
            }
        }
    }

    init {
        scope.launch {
            try {
                _refreshTrigger.flatMapLatest {
                    itemsSource
                }.collect { list ->
                    _allItems.value = list
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Throwable) {
                println("Error collecting transactions: ${e.message}")
            }
        }

        scope.launch {
            combine(
                _allItems,
                _currentMonth,
                _currentYear,
            ) { list, month, year ->
                list.filter { tx ->
                    if (tx.isRecurring) {
                        // Recurring transactions appear from their start month onwards
                        (tx.targetYear < year) ||
                            (tx.targetYear == year && tx.targetMonth <= month)
                    } else {
                        // Non-recurring transactions appear only in their target month
                        tx.targetMonth == month && tx.targetYear == year
                    }
                }
            }.collect { filtered ->
                _items.value = filtered

                val (inc, exp) = computeTotals(filtered)
                _totalIncomeText.value = "R$ ${formatTwoDecimals(inc)}"
                _totalExpenseText.value = "R$ ${formatTwoDecimals(exp)}"

                val selectedMonth = _currentMonth.value
                val selectedYear = _currentYear.value

                val accumulatedItems = _allItems.value.filter { tx ->
                    // Include transactions up to and including the selected month
                    (tx.targetYear < selectedYear) ||
                        (tx.targetYear == selectedYear && tx.targetMonth <= selectedMonth)
                }

                // For savings calculation, only count income up to current month
                // (future income should not be counted)
                val (currentMonth, currentYear) = currentMonthYear()
                val (allInc, allExp) = computeTotalsForSavingsWithTargetMonth(
                    accumulatedItems,
                    currentMonth = currentMonth,
                    currentYear = currentYear,
                )
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
        onSignInRequired: () -> Unit,
        onResult: ((BackupResult) -> Unit)? = null,
    ) {
        onResultCallback = onResult
        _backupResult.value = null
        scope.launch {
            try {
                when (val result = createSpreadsheet()) {
                    is BackupResult.SignInRequired -> {
                        _backupResult.value = result
                        onResult?.invoke(result)
                        onSignInRequired()
                    }

                    else -> {
                        _backupResult.value = result
                        onResult?.invoke(result)
                    }
                }
            } catch (e: Exception) {
                val error = BackupResult.Error("Falha ao criar backup", e)
                _backupResult.value = error
                onResult?.invoke(error)
            }
        }
    }

    fun onSignInSuccess() {
        scope.launch {
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
        return exportDataToSpreadsheetUseCase?.execute(
            Clock.System.now().toEpochMilliseconds(),
        ) ?: BackupModule.createSpreadSheetUseCase.execute(
            Clock.System.now().toEpochMilliseconds(),
        )
    }

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
                when (
                    val result =
                        BackupModule.listBackupSpreadsheetsUseCase.execute()
                ) {
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

                if (result is ImportResult.Success) {
                    // Force refresh of items from the source after successful import
                    refreshItems()
                }

                if (result is ImportResult.SignInRequired) {
                    onSignInRequired()
                }
            } catch (e: Exception) {
                _restoreResult.value =
                    ImportResult.Error("Falha ao restaurar backup", e)
            }
        }
    }

    /**
     * Forces a refresh of items from the source.
     * This is useful after data has been modified externally (e.g., after restore).
     */
    fun refreshItems() {
        _refreshTrigger.value++
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
