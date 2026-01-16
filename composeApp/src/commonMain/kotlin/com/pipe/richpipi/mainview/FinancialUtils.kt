package com.pipe.richpipi.mainview

import domain.model.Transaction
import domain.model.TransactionType
import kotlin.math.abs
import kotlin.math.round

/**
 * Format amount in cents to two-decimal string using dot.
 */
fun formatMoneyFromCents(amountCents: Long): String {
    val negative = amountCents < 0
    val absCents = abs(amountCents)
    val whole = absCents / 100
    val fraction = (absCents % 100).toString().padStart(2, '0')
    val sign = if (negative) "-" else ""
    return "$sign$whole.$fraction"
}

/**
 * Keep backward-compatible name used by viewmodels: formatTwoDecimals (accepts Double previously).
 * Provide a function that formats a double by converting to cents then delegating to
 * formatMoneyFromCents.
 */
fun formatTwoDecimals(value: Double): String {
    val cents = round(value * 100).toLong()
    return formatMoneyFromCents(cents)
}

/**
 * Compute total income and expense from a list of Transactions using amountCents and type.
 * Returns Pair(incomeDouble, expenseDouble) — legacy API returning Double amounts in units.
 */
fun computeTotals(items: List<Transaction>): Pair<Double, Double> {
    var incomeCents = 0L
    var expenseCents = 0L
    for (item in items) {
        when (item.type) {
            TransactionType.INCOME -> incomeCents += item.amountCents
            TransactionType.EXPENSE -> expenseCents += item.amountCents
        }
    }
    return Pair(incomeCents / 100.0, expenseCents / 100.0)
}
