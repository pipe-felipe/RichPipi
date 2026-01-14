package com.pipe.richpipi.form

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import data.local.entity.ItemEntity

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

    // Items exposed to UI
    private val _items = MutableStateFlow<List<ItemEntity>>(emptyList())
    val items: StateFlow<List<ItemEntity>> = _items.asStateFlow()

    // Handler set by platform (Android) to perform DB insertions
    private var addItemHandler: (suspend (ItemEntity) -> Long)? = null

    fun setAddItemHandler(handler: suspend (ItemEntity) -> Long) {
        addItemHandler = handler
    }

    fun setItems(list: List<ItemEntity>) {
        _items.value = list
    }

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
        // create item entity
        val item = ItemEntity(
            name = "${uiState.value.transactionType} - ${uiState.value.quantity}",
            description = "${uiState.value.notes} | date: $date",
            createdAt = Clock.System.now().toEpochMilliseconds()
        )

        // Launch insertion using handler set by the Android layer
        val handler = addItemHandler
        if (handler != null) {
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    val id = handler(item)
                    println("Inserted item id: $id")
                } catch (t: Throwable) {
                    println("Error inserting item: ${t.message}")
                }
            }
        } else {
            println("No DB handler configured — item not persisted. Item: $item")
        }
    }
}
