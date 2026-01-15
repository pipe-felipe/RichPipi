package com.pipe.richpipi.mainview

import domain.model.Transaction
import domain.model.TransactionType
import kotlin.test.Test
import kotlin.test.assertEquals

class MainScreenViewModelTest {

    @Test
    fun `compute totals sums income and expense correctly`() {
        val items = listOf(
            Transaction(id = 1, amountCents = 100000, type = TransactionType.INCOME), // 1000.00
            Transaction(id = 2, amountCents = 25050, type = TransactionType.INCOME),  // 250.50
            Transaction(id = 3, amountCents = 3000, type = TransactionType.EXPENSE),  // 30.00
            Transaction(id = 4, amountCents = 475, type = TransactionType.EXPENSE),   // 4.75
            Transaction(id = 6, amountCents = 1025, type = TransactionType.INCOME)    // 10.25
        )

        val vm = MainScreenViewModel()

        val (income, expense) = vm.computeTotalsForTest(items)
        val saving = income - expense

        assertEquals(1260.75, income)
        assertEquals(34.75, expense)
        assertEquals(1226.0, saving)
    }

    @Test
    fun `compute totals handles invalid names gracefully`() {
        // With new domain model, invalid strings are gone; simulate zero amounts and one valid
        val items = listOf(
            Transaction(id = 1, amountCents = 0, type = TransactionType.INCOME),
            Transaction(id = 2, amountCents = 0, type = TransactionType.EXPENSE),
            Transaction(id = 3, amountCents = 50000, type = TransactionType.INCOME) // 500.00
        )

        val vm = MainScreenViewModel()

        val (income, expense) = vm.computeTotalsForTest(items)
        val saving = income - expense

        assertEquals(500.0, income)
        assertEquals(0.0, expense)
        assertEquals(500.0, saving)
    }

    @Test
    fun `saving can be negative when expenses exceed income`() {
        val items = listOf(
            Transaction(id = 1, amountCents = 1000, type = TransactionType.INCOME), // 10.00
            Transaction(id = 2, amountCents = 2500, type = TransactionType.EXPENSE) // 25.00
        )

        val vm = MainScreenViewModel()

        val (income, expense) = vm.computeTotalsForTest(items)
        val saving = income - expense

        assertEquals(10.0, income)
        assertEquals(25.0, expense)
        assertEquals(-15.0, saving)
    }
}
