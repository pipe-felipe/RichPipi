package com.pipe.richpipi.form

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

enum class TransactionType {
    EXPENSE,
    INCOME
}

enum class ExpenseCategory {
    TRANSPORT,
    GIFT,
    RECURRING,
    FOOD,
    STUFF,
    MEDICINE,
    CLOTHES
}

enum class IncomeCategory {
    SALARY,
    GIFT,
    INVESTMENT,
    OTHER
}

/**
 * Represents the state of the form UI.
 */
data class FormUiState(
    val date: String = "",
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val expenseCategory: ExpenseCategory = ExpenseCategory.FOOD,
    val incomeCategory: IncomeCategory = IncomeCategory.SALARY,
    val quantity: String = "",
    val notes: String = "",
    val isRecurring: Boolean = false,
    val isQuantityError: Boolean = false
)

/**
 * ViewModel to handle the business logic and state of the form.
 */
class TransactionalViewModel : ViewModel() {

    // Private mutable state flow
    private val _uiState = MutableStateFlow(FormUiState())
    // Public read-only state flow
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    /**
     * Called when the transaction type changes.
     */
    fun onTransactionTypeChange(newType: TransactionType) {
        _uiState.update { currentState ->
            currentState.copy(transactionType = newType)
        }
    }

    /**
     * Called when the date field value changes.
     */
    fun onDateChange(newText: String) {
        _uiState.update { currentState ->
            currentState.copy(date = newText)
        }
    }

    /**
     * Called when the expense category field value changes.
     */
    fun onExpenseCategoryChange(newCategory: ExpenseCategory) {
        _uiState.update { currentState ->
            currentState.copy(expenseCategory = newCategory)
        }
    }

    /**
     * Called when the income category field value changes.
     */
    fun onIncomeCategoryChange(newCategory: IncomeCategory) {
        _uiState.update { currentState ->
            currentState.copy(incomeCategory = newCategory)
        }
    }

    /**
     * Called when the quantity field value changes.
     */
    fun onQuantityChange(input: String) {
        val isValid = input
            .replace(",", ".")
            .toFloatOrNull() != null

        _uiState.update { currentState ->
            currentState.copy(
                quantity = input,
                isQuantityError = input.isNotBlank() && !isValid
            )
        }
    }


    /**
     * Called when the notes field value changes.
     */
    fun onNotesChange(newText: String) {
        _uiState.update { currentState ->
            currentState.copy(notes = newText)
        }
    }

    /**
     * Called when the recurring switch is toggled.
     */
    fun onRecurringChange(isChecked: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isRecurring = isChecked)
        }
    }

    /**
     * Called when the form is submitted.
     */
    @OptIn(ExperimentalTime::class)
    fun submit() {
        // You can add your submission logic here.
        val date = uiState.value.date.ifBlank {
            val today = Clock.System.now()
            today.toString()
        }
        println("Submitted value: $date")

        // You might want to reset the state after submission
        // _uiState.value = FormUiState()
    }
}
