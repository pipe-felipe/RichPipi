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
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TransactionalViewModelTest {

    private class CapturingRepository : TransactionRepository {
        private val itemsFlow = MutableStateFlow<List<Transaction>>(emptyList())

        var makeCalled = false
            private set

        override fun getTransactions(): Flow<List<Transaction>> = itemsFlow.asStateFlow()

        override fun getTransactionsForMonth(
            monthStartMillis: Long,
            monthEndExclusiveMillis: Long
        ): Flow<List<Transaction>> = flowOf(emptyList())

        override suspend fun makeTransaction(transaction: Transaction): Long {
            makeCalled = true
            return 123L
        }

        override suspend fun deleteTransaction(id: Int): Int = 1
    }

    @Test
    fun `onQuantityChange marks error for invalid number`() {
        val repo = CapturingRepository()
        val vm = TransactionalViewModel(
            addItemUseCase = MakeTransactionUseCase(repo),
            getAllItemsUseCase = GetTransactions(repo),
            deleteItemUseCase = DeleteTransactionUseCase(repo)
        )

        vm.onQuantityChange("abc")
        assertTrue(vm.uiState.value.isQuantityError)

        vm.onQuantityChange("10,50")
        assertFalse(vm.uiState.value.isQuantityError)
    }

    @Test
    fun `submit does not call add use case when category missing`() {
        val repo = CapturingRepository()
        val vm = TransactionalViewModel(
            addItemUseCase = MakeTransactionUseCase(repo),
            getAllItemsUseCase = GetTransactions(repo),
            deleteItemUseCase = DeleteTransactionUseCase(repo)
        )

        vm.onTransactionTypeChange(TransactionType.EXPENSE)
        vm.onQuantityChange("10")

        vm.submit()

        // submit should early-return synchronously
        assertFalse(repo.makeCalled)
    }
}
