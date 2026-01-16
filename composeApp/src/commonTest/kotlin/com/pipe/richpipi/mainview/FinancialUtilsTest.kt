package com.pipe.richpipi.mainview

import domain.model.Transaction
import domain.model.TransactionType
import kotlin.test.Test
import kotlin.test.assertEquals

class FinancialUtilsTest {
    @Test
    fun `formatMoneyFromCents formats properly`() {
        assertEquals("0.00", formatMoneyFromCents(0))
        assertEquals("1.23", formatMoneyFromCents(123))
        assertEquals("-1.23", formatMoneyFromCents(-123))
        assertEquals("1000.00", formatMoneyFromCents(100000))
    }

    @Test
    fun `computeTotals sums correctly`() {
        val items = listOf(
            Transaction(amountCents = 100000, type = TransactionType.INCOME),
            Transaction(amountCents = 25050, type = TransactionType.INCOME),
            Transaction(amountCents = 3000, type = TransactionType.EXPENSE),
        )

        val (inc, exp) = computeTotals(items)
        assertEquals(1250.50, inc)
        assertEquals(30.0, exp)
    }
}
