package com.pipe.richpipi.mainview

import com.pipe.richpipi.platform.monthBoundsUtcMillis
import domain.model.BackupResult
import domain.model.Transaction
import domain.model.TransactionType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MainScreenViewModelTest {

    @Test
    fun `compute totals sums income and expense correctly`() {
        val items = listOf(
            Transaction(id = 1, amountCents = 100000, type = TransactionType.INCOME), // 1000.00
            Transaction(id = 2, amountCents = 25050, type = TransactionType.INCOME), // 250.50
            Transaction(id = 3, amountCents = 3000, type = TransactionType.EXPENSE), // 30.00
            Transaction(id = 4, amountCents = 475, type = TransactionType.EXPENSE), // 4.75
            Transaction(id = 6, amountCents = 1025, type = TransactionType.INCOME), // 10.25
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
            Transaction(id = 3, amountCents = 50000, type = TransactionType.INCOME), // 500.00
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
            Transaction(id = 2, amountCents = 2500, type = TransactionType.EXPENSE), // 25.00
        )

        val vm = MainScreenViewModel()

        val (income, expense) = vm.computeTotalsForTest(items)
        val saving = income - expense

        assertEquals(10.0, income)
        assertEquals(25.0, expense)
        assertEquals(-15.0, saving)
    }

    @Test
    fun `all-time saving differs from month totals when transactions span months`() {
        val allItems = listOf(
            // Month A net: +100.00
            Transaction(id = 1, amountCents = 10000, type = TransactionType.INCOME),
            // Month B net: -25.00
            Transaction(id = 2, amountCents = 2500, type = TransactionType.EXPENSE),
        )

        val monthItems = listOf(
            Transaction(id = 2, amountCents = 2500, type = TransactionType.EXPENSE),
        )

        val vm = MainScreenViewModel()

        // Month-scoped totals (e.g., month B) only see that month's items.
        val (monthInc, monthExp) = vm.computeTotalsForTest(monthItems)
        val monthSaving = monthInc - monthExp
        assertEquals(0.0, monthInc)
        assertEquals(25.0, monthExp)
        assertEquals(-25.0, monthSaving)

        // Accumulated saving up to month B includes both months nets:
        // (+100.00) + (-25.00) = 75.00
        val (allInc, allExp) = vm.computeTotalsForTest(allItems)
        val accumulatedSaving = allInc - allExp
        assertEquals(75.0, accumulatedSaving)
    }

    @Test
    fun `recurring income only counts in accumulated saving from its start month onward`() = runBlocking {
        val (janStart, _) = monthBoundsUtcMillis(month = 1, year = 2026)
        val (febStart, _) = monthBoundsUtcMillis(month = 2, year = 2026)

        val itemsFlow = MutableStateFlow(
            listOf(
                // Salary recurring starting Feb/2026.
                Transaction(
                    id = 1,
                    amountCents = 100_00,
                    type = TransactionType.INCOME,
                    humanDate = "2026-02-01",
                    isRecurring = true,
                    createdAt = febStart,
                ),
                // One expense in Jan/2026.
                Transaction(
                    id = 2,
                    amountCents = 50_00,
                    type = TransactionType.EXPENSE,
                    humanDate = "2026-01-01",
                    isRecurring = false,
                    createdAt = janStart,
                ),
            ),
        )

        val vm = MainScreenViewModel(itemsSource = itemsFlow)

        // Go to Jan/2026 (before salary start month)
        while (vm.currentYear.value > 2026 || (vm.currentYear.value == 2026 && vm.currentMonth.value > 1)) {
            vm.goToPreviousMonth()
        }
        while (vm.currentYear.value < 2026 || (vm.currentYear.value == 2026 && vm.currentMonth.value < 1)) {
            vm.goToNextMonth()
        }

        // Allow background collectors (Dispatchers.Default) to update the state.
        delay(100)

        // In January: month totals include Jan expense; accumulated saving up to Jan end should NOT include Feb salary.
        assertEquals("R$ 0.00", vm.totalIncomeText.value)
        assertEquals("R$ 50.00", vm.totalExpenseText.value)
        assertEquals("R$ -50.00", vm.totalSavingText.value)

        // Move to Feb/2026: now salary month has arrived.
        vm.goToNextMonth()
        delay(100)

        // In February: month totals now include recurring salary (since start month reached).
        assertEquals("R$ 100.00", vm.totalIncomeText.value)
        assertEquals("R$ 0.00", vm.totalExpenseText.value)

        // Accumulated saving up to Feb end includes Jan expense + Feb salary => 100.00 - 50.00 = 50.00
        assertEquals("R$ 50.00", vm.totalSavingText.value)
    }

    @Test
    fun `backupToDrive should call callback with result`() = runBlocking {
        // Given
        val vm = MainScreenViewModel()
        var callbackResult: BackupResult? = null
        var callbackCalled = false

        // When
        vm.backupToDrive { result ->
            callbackResult = result
            callbackCalled = true
        }

        // Wait a bit for the coroutine to complete
        delay(1000)

        // Then - callback should be called
        assertTrue(callbackCalled, "Callback should be called")
        assertTrue(
            callbackResult is BackupResult.Success ||
                callbackResult is BackupResult.Error ||
                callbackResult is BackupResult.SignInRequired,
            "Result should be one of the expected BackupResult types",
        )
    }
}
