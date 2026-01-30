package com.pipe.richpipi.form

import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import domain.model.Transaction
import domain.model.TransactionType
import domain.repository.TransactionRepository
import domain.usecase.DeleteTransactionUseCase
import domain.usecase.GetTransactions
import domain.usecase.MakeTransactionUseCase
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionalViewModelAndroidTest {

    @Test
    fun submit_calls_add_use_case_with_parsed_cents_and_month_start_date() {
        var capturedTransaction: Transaction? = null

        val repo = mock<TransactionRepository> {
            every { getTransactions() } returns flowOf(emptyList())
            every { getTransactionsForMonth(any(), any()) } returns flowOf(emptyList())
            everySuspend { makeTransaction(any()) } calls { (tx: Transaction) ->
                capturedTransaction = tx
                123L
            }
            everySuspend { deleteTransaction(any()) } returns 1
            everySuspend { deleteAllTransactions() } returns 0
            everySuspend { insertTransactions(any()) } returns emptyList()
        }

        val vm = TransactionalViewModel(
            addItemUseCase = MakeTransactionUseCase(repo),
            getAllItemsUseCase = GetTransactions(repo),
            deleteItemUseCase = DeleteTransactionUseCase(repo),
        )

        vm.onTransactionTypeChange(TransactionType.INCOME)
        vm.onIncomeCategoryChange(IncomeCategory.SALARY)
        vm.onQuantityChange("10,50")
        vm.onNotesChange("note")
        vm.onRecurringChange(true)

        // Correct the expectedHumanDate to match the current test date
        val month = 1
        val year = 2026
        val expectedHumanDate = "$year-${month.toString().padStart(2, '0')}-24"

        // Set the date explicitly to match expectedHumanDate
        vm.onDateChange(expectedHumanDate)

        vm.submit(month = month, year = year)

        // Wait for async operation to complete
        val deadline = System.currentTimeMillis() + 2_000
        while (capturedTransaction == null && System.currentTimeMillis() < deadline) {
            Thread.sleep(10)
        }

        assertNotNull("Transaction was not made within timeout", capturedTransaction)
        val tx = capturedTransaction!!

        assertEquals(1050L, tx.amountCents)
        assertEquals(TransactionType.INCOME, tx.type)
        assertEquals(expectedHumanDate, tx.humanDate)
        assertTrue(tx.isRecurring)
    }
}
