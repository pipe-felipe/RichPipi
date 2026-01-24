package com.pipe.richpipi.form

import domain.model.Transaction
import domain.model.TransactionType
import domain.repository.TransactionRepository
import domain.usecase.DeleteTransactionUseCase
import domain.usecase.GetTransactions
import domain.usecase.MakeTransactionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionalViewModelAndroidTest {

    private class CapturingRepository : TransactionRepository {
        private val itemsFlow = MutableStateFlow<List<Transaction>>(emptyList())

        @Volatile
        var makeCalled = false

        @Volatile
        var lastMadeTransaction: Transaction? = null

        override fun getTransactions(): Flow<List<Transaction>> = itemsFlow.asStateFlow()

        override fun getTransactionsForMonth(monthStartMillis: Long, monthEndExclusiveMillis: Long): Flow<List<Transaction>> = flowOf(emptyList())

        override suspend fun makeTransaction(transaction: Transaction): Long {
            makeCalled = true
            lastMadeTransaction = transaction
            return 123L
        }

        override suspend fun deleteTransaction(id: Int): Int = 1
    }

    @Test
    fun submit_calls_add_use_case_with_parsed_cents_and_month_start_date() {
        val repo = CapturingRepository()
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

        val month = 2
        val year = 2026
        val expectedHumanDate = "$year-${month.toString().padStart(2, '0')}-01"

        vm.submit(month = month, year = year)

        val deadline = System.currentTimeMillis() + 2_000
        while (!repo.makeCalled && System.currentTimeMillis() < deadline) {
            Thread.sleep(10)
        }

        assertTrue(repo.makeCalled)
        val tx = repo.lastMadeTransaction
        assertNotNull(tx)

        assertEquals(1050L, tx!!.amountCents)
        assertEquals(TransactionType.INCOME, tx.type)
        assertEquals(expectedHumanDate, tx.humanDate)
        assertTrue(tx.isRecurring)
    }
}
