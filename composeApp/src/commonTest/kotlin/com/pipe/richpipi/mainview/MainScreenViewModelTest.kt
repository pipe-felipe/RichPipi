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
            // OTHER should be ignored; create a transaction with an unrelated type? domain only has INCOME/EXPENSE so skip
            Transaction(id = 6, amountCents = 1025, type = TransactionType.INCOME)    // 10.25
        )

        val vm = MainScreenViewModel()

        val (income, expense) = vm.computeTotalsForTest(items)

        assertEquals(1260.75, income)
        assertEquals(34.75, expense)
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

        assertEquals(500.0, income)
        assertEquals(0.0, expense)
    }
}
