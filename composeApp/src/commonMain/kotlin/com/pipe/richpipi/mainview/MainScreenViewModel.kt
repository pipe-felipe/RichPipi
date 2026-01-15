package com.pipe.richpipi.mainview

import domain.model.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round

/**
 * Lightweight view-model-like class for the main screen UI.
 * It is not an AndroidX ViewModel so it can be instantiated from common code easily.
 */
class MainScreenViewModel(
    itemsSource: Flow<List<Item>> = emptyFlow(),
    private val onDeleteItem: (Int) -> Unit = {}
) {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

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

    private fun computeTotals(items: List<Item>): Pair<Double, Double> {
        var income = 0.0
        var expense = 0.0
        for (item in items) {
            val amt = parseAmountFromName(item.name) ?: continue
            val prefix = item.name.trim().split(" ").firstOrNull()?.uppercase() ?: ""
            when {
                prefix.startsWith("INCOME") -> income += amt
                prefix.startsWith("EXPENSE") -> expense += amt
                else -> {
                    // Unknown prefix, ignore
                }
            }
        }
        return Pair(income, expense)
    }

    private fun parseAmountFromName(name: String): Double? {
        val parts = name.split(" - ")
        if (parts.size < 2) return null
        val qtyStr = parts.last().trim().replace(",", ".")
        return qtyStr.toDoubleOrNull()
    }

    private fun formatTwoDecimals(value: Double): String {
        val negative = value < 0
        val cents = round(value * 100).toLong()
        val absCents = abs(cents)
        val whole = absCents / 100
        val fraction = (absCents % 100).toString().padStart(2, '0')
        val sign = if (negative) "-" else ""
        return "$sign$whole.$fraction"
    }
}