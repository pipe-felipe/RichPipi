package com.pipe.richpipi.mainview

import domain.model.Transaction
import kotlin.test.Test
import kotlin.test.assertEquals

class MainScreenViewModelTest {

    @Test
    fun `compute totals sums income and expense correctly`() {
        val items = listOf(
            Transaction(id = 1, value = "INCOME Salary - 1000"),
            Transaction(id = 2, value = "INCOME Bonus - 250.5"),
            Transaction(id = 3, value = "EXPENSE Food - 30"),
            Transaction(id = 4, value = "EXPENSE Coffee - 4.75"),
            Transaction(id = 5, value = "OTHER Note - 999"), // should be ignored
            Transaction(id = 6, value = "INCOME Gift - 10,25") // comma as decimal
        )

        val vm = MainScreenViewModel()

        val (income, expense) = vm.computeTotalsForTest(items)

        assertEquals(1260.75, income)
        assertEquals(34.75, expense)
    }

    @Test
    fun `compute totals handles invalid names gracefully`() {
        val items = listOf(
            Transaction(id = 1, value = "INCOME - "),
            Transaction(id = 2, value = "EXPENSE - abc"),
            Transaction(id = 3, value = "INCOME Rent - 500.0")
        )

        val vm = MainScreenViewModel()

        val (income, expense) = vm.computeTotalsForTest(items)

        assertEquals(500.0, income)
        assertEquals(0.0, expense)
    }
}
