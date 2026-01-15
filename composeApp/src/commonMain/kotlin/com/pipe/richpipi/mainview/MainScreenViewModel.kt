package com.pipe.richpipi.mainview

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


    internal fun computeTotalsForTest(items: List<Transaction>): Pair<Double, Double> =
        computeTotals(items)
}