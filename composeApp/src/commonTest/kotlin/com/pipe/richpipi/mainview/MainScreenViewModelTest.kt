package com.pipe.richpipi.mainview

import domain.model.Item
import kotlin.test.Test
import kotlin.test.assertEquals

class MainScreenViewModelTest {

    @Test
    fun `compute totals sums income and expense correctly`() {
        val items = listOf(
            Item(id = 1, name = "INCOME Salary - 1000"),
            Item(id = 2, name = "INCOME Bonus - 250.5"),
            Item(id = 3, name = "EXPENSE Food - 30"),
            Item(id = 4, name = "EXPENSE Coffee - 4.75"),
            Item(id = 5, name = "OTHER Note - 999"), // should be ignored
            Item(id = 6, name = "INCOME Gift - 10,25") // comma as decimal
        )

        val vm = MainScreenViewModel()

        val (income, expense) = vm.computeTotalsForTest(items)

        assertEquals(1260.75, income)
        assertEquals(34.75, expense)
    }

    @Test
    fun `compute totals handles invalid names gracefully`() {
        val items = listOf(
            Item(id = 1, name = "INCOME - "),
            Item(id = 2, name = "EXPENSE - abc"),
            Item(id = 3, name = "INCOME Rent - 500.0")
        )

        val vm = MainScreenViewModel()

        val (income, expense) = vm.computeTotalsForTest(items)

        assertEquals(500.0, income)
        assertEquals(0.0, expense)
    }
}
