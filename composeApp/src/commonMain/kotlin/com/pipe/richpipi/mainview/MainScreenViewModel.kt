package com.pipe.richpipi.mainview

import com.pipe.richpipi.platform.currentMonthYear
import domain.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

/**
 * Lightweight view-model-like class for the main screen UI.
 * It is not an AndroidX ViewModel so it can be instantiated from common code easily.
 */
class MainScreenViewModel(
    itemsSource: Flow<List<Transaction>> = emptyFlow(),
    private val onDeleteItem: (Int) -> Unit = {}
) {
    private val _items = MutableStateFlow<List<Transaction>>(emptyList())
    val items: StateFlow<List<Transaction>> = _items.asStateFlow()

    private val _totalIncomeText = MutableStateFlow("R$ 0.00")
    private val _totalExpenseText = MutableStateFlow("R$ 0.00")
    val totalIncomeText: StateFlow<String> = _totalIncomeText.asStateFlow()
    val totalExpenseText: StateFlow<String> = _totalExpenseText.asStateFlow()

    // Current month and year for navigation
    private val initialMonthYear = currentMonthYear()
    private val _currentMonth = MutableStateFlow(initialMonthYear.first)
    private val _currentYear = MutableStateFlow(initialMonthYear.second)
    val currentMonth: StateFlow<Int> = _currentMonth.asStateFlow()
    val currentYear: StateFlow<Int> = _currentYear.asStateFlow()

    private val _currentMonthYearText = MutableStateFlow(formatMonthYear(initialMonthYear.first, initialMonthYear.second))
    val currentMonthYearText: StateFlow<String> = _currentMonthYearText.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            try {
                itemsSource.collect { list ->
                    _items.value = list
                    val (inc, exp) = computeTotals(list)
                    _totalIncomeText.value = "R$ ${formatTwoDecimals(inc)}"
                    _totalExpenseText.value = "R$ ${formatTwoDecimals(exp)}"
                }
            } catch (_: Throwable) {
                // ignore
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
        _currentMonthYearText.value = formatMonthYear(_currentMonth.value, _currentYear.value)
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
        _currentMonthYearText.value = formatMonthYear(_currentMonth.value, _currentYear.value)
    }

    private fun formatMonthYear(month: Int, year: Int): String {
        val monthNames = listOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )
        return "${monthNames[month - 1]} $year"
    }

    internal fun computeTotalsForTest(items: List<Transaction>): Pair<Double, Double> =
        computeTotals(items)
}