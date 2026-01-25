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

    @Test
    fun `computeTotalsForSavings excludes future income`() {
        val currentMonthEnd = 1000L // Simulated current month end

        val items = listOf(
            // Past income (should be counted)
            Transaction(amountCents = 100000, type = TransactionType.INCOME, createdAt = 500L),
            // Future income (should NOT be counted)
            Transaction(amountCents = 50000, type = TransactionType.INCOME, createdAt = 1500L),
            // Past expense (should be counted)
            Transaction(amountCents = 3000, type = TransactionType.EXPENSE, createdAt = 500L),
            // Future expense (should still be counted - expenses always count)
            Transaction(amountCents = 2000, type = TransactionType.EXPENSE, createdAt = 1500L),
        )

        val (inc, exp) = computeTotalsForSavings(items, incomeEndExclusiveMillis = currentMonthEnd)

        // Only past income should be counted (100000 cents = 1000.00)
        assertEquals(1000.0, inc)
        // All expenses should be counted (3000 + 2000 = 5000 cents = 50.00)
        assertEquals(50.0, exp)
    }

    @Test
    fun `computeTotalsForSavings counts income at boundary correctly`() {
        val currentMonthEnd = 1000L

        val items = listOf(
            // Income exactly at boundary (should NOT be counted - exclusive)
            Transaction(amountCents = 10000, type = TransactionType.INCOME, createdAt = 1000L),
            // Income just before boundary (should be counted)
            Transaction(amountCents = 20000, type = TransactionType.INCOME, createdAt = 999L),
        )

        val (inc, _) = computeTotalsForSavings(items, incomeEndExclusiveMillis = currentMonthEnd)

        // Only income before boundary should be counted (20000 cents = 200.00)
        assertEquals(200.0, inc)
    }

    @Test
    fun `computeTotalsForSavings with no future income behaves like computeTotals`() {
        val currentMonthEnd = 2000L

        val items = listOf(
            Transaction(amountCents = 100000, type = TransactionType.INCOME, createdAt = 500L),
            Transaction(amountCents = 25050, type = TransactionType.INCOME, createdAt = 600L),
            Transaction(amountCents = 3000, type = TransactionType.EXPENSE, createdAt = 700L),
        )

        val (incSavings, expSavings) = computeTotalsForSavings(items, incomeEndExclusiveMillis = currentMonthEnd)
        val (incTotal, expTotal) = computeTotals(items)

        assertEquals(incTotal, incSavings)
        assertEquals(expTotal, expSavings)
    }

    @Test
    fun `computeTotalsForSavingsWithTargetMonth excludes future income`() {
        val currentMonth = 1
        val currentYear = 2026

        val items = listOf(
            // Past income - Dec 2025 (should be counted)
            Transaction(amountCents = 100000, type = TransactionType.INCOME, targetMonth = 12, targetYear = 2025),
            // Current income - Jan 2026 (should be counted)
            Transaction(amountCents = 50000, type = TransactionType.INCOME, targetMonth = 1, targetYear = 2026),
            // Future income - Feb 2026 (should NOT be counted)
            Transaction(amountCents = 30000, type = TransactionType.INCOME, targetMonth = 2, targetYear = 2026),
            // Past expense (should be counted)
            Transaction(amountCents = 3000, type = TransactionType.EXPENSE, targetMonth = 12, targetYear = 2025),
            // Future expense (should still be counted - expenses always count)
            Transaction(amountCents = 2000, type = TransactionType.EXPENSE, targetMonth = 2, targetYear = 2026),
        )

        val (inc, exp) = computeTotalsForSavingsWithTargetMonth(items, currentMonth, currentYear)

        // Past + current income should be counted (100000 + 50000 = 1500.00)
        assertEquals(1500.0, inc)
        // All expenses should be counted (3000 + 2000 = 50.00)
        assertEquals(50.0, exp)
    }

    @Test
    fun `computeTotalsForSavingsWithTargetMonth counts income at boundary correctly`() {
        val currentMonth = 6
        val currentYear = 2026

        val items = listOf(
            // Income in current month (should be counted)
            Transaction(amountCents = 10000, type = TransactionType.INCOME, targetMonth = 6, targetYear = 2026),
            // Income in next month same year (should NOT be counted)
            Transaction(amountCents = 20000, type = TransactionType.INCOME, targetMonth = 7, targetYear = 2026),
            // Income in previous month (should be counted)
            Transaction(amountCents = 30000, type = TransactionType.INCOME, targetMonth = 5, targetYear = 2026),
        )

        val (inc, _) = computeTotalsForSavingsWithTargetMonth(items, currentMonth, currentYear)

        // Current + previous month income (10000 + 30000 = 400.00)
        assertEquals(400.0, inc)
    }

    @Test
    fun `computeTotalsForSavingsWithTargetMonth handles year boundary`() {
        val currentMonth = 1
        val currentYear = 2026

        val items = listOf(
            // Income in previous year (should be counted)
            Transaction(amountCents = 10000, type = TransactionType.INCOME, targetMonth = 12, targetYear = 2025),
            // Income in current month (should be counted)
            Transaction(amountCents = 20000, type = TransactionType.INCOME, targetMonth = 1, targetYear = 2026),
            // Income in next year (should NOT be counted)
            Transaction(amountCents = 30000, type = TransactionType.INCOME, targetMonth = 1, targetYear = 2027),
        )

        val (inc, _) = computeTotalsForSavingsWithTargetMonth(items, currentMonth, currentYear)

        // Previous year + current month (10000 + 20000 = 300.00)
        assertEquals(300.0, inc)
    }
}
