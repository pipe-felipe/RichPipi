package com.pipe.richpipi.form

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import domain.model.Item
import domain.usecase.AddItemUseCase
import domain.usecase.GetAllItemsUseCase

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
class TransactionalViewModel(
    private val addItemUseCase: AddItemUseCase,
    private val getAllItemsUseCase: GetAllItemsUseCase
) : ViewModel() {

    // Private mutable state flow
    private val _uiState = MutableStateFlow(FormUiState())
    // Public read-only state flow
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    // Items exposed to UI (as Flow from domain use case)
    val items: Flow<List<Item>> = getAllItemsUseCase()

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
        // Build item from uiState
        val date = uiState.value.date.ifBlank {
            val today = Clock.System.now()
            today.toString()
        }
        // create domain item
        val item = Item(
            name = "${uiState.value.transactionType} - ${uiState.value.quantity}",
            description = "${uiState.value.notes} | date: $date",
            createdAt = Clock.System.now().toEpochMilliseconds()
        )

        // Insert using domain use-case
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val id = addItemUseCase(item)
                println("Inserted item id: $id")
            } catch (t: Throwable) {
                println("Error inserting item: ${t.message}")
            }
        }
    }
}
