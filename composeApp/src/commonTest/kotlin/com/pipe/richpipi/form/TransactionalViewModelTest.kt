package com.pipe.richpipi.form

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import domain.model.TransactionType
import domain.repository.TransactionRepository
import domain.usecase.DeleteTransactionUseCase
import domain.usecase.GetTransactions
import domain.usecase.MakeTransactionUseCase
import kotlinx.coroutines.flow.flowOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TransactionalViewModelTest {

    @Test
    fun `onQuantityChange marks error for invalid number`() {
        val repo = mock<TransactionRepository> {
            everySuspend { getTransactions() } returns flowOf(emptyList())
        }

        val vm = TransactionalViewModel(
            addItemUseCase = MakeTransactionUseCase(repo),
            getAllItemsUseCase = GetTransactions(repo),
            deleteItemUseCase = DeleteTransactionUseCase(repo),
        )

        vm.onQuantityChange("abc")
        assertTrue(vm.uiState.value.isQuantityError)

        vm.onQuantityChange("10,50")
        assertFalse(vm.uiState.value.isQuantityError)
    }

    @Test
    fun `submit does not call add use case when category missing`() {
        val repo = mock<TransactionRepository> {
            everySuspend { getTransactions() } returns flowOf(emptyList())
            everySuspend { makeTransaction(any()) } returns 123L
        }

        val vm = TransactionalViewModel(
            addItemUseCase = MakeTransactionUseCase(repo),
            getAllItemsUseCase = GetTransactions(repo),
            deleteItemUseCase = DeleteTransactionUseCase(repo),
        )

        vm.onTransactionTypeChange(TransactionType.EXPENSE)
        vm.onQuantityChange("10")

        vm.submit(month = 1, year = 2026)

        // submit should early-return synchronously without calling makeTransaction
        verifySuspend(mode = VerifyMode.not) {
            repo.makeTransaction(any())
        }
    }
}
