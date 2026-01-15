package com.pipe.richpipi.form

import com.pipe.richpipi.platform.monthBoundsUtcMillis
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
import kotlin.math.round
import domain.model.Transaction
import domain.model.TransactionType as DomainTransactionType
import domain.usecase.MakeTransactionUseCase
import domain.usecase.GetTransactions
import domain.usecase.DeleteTransactionUseCase

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
    val transactionType: DomainTransactionType = DomainTransactionType.EXPENSE,
    val expenseCategory: ExpenseCategory? = null,
    val incomeCategory: IncomeCategory? = null,
    val quantity: String = "",
    val notes: String = "",
    val isRecurring: Boolean = false,
    val isQuantityError: Boolean = false
)

/**
 * ViewModel to handle the business logic and state of the form.
 */
class TransactionalViewModel(
    private val addItemUseCase: MakeTransactionUseCase,
    private val getAllItemsUseCase: GetTransactions,
    private val deleteItemUseCase: DeleteTransactionUseCase
) : ViewModel() {

    // Private mutable state flow
    private val _uiState = MutableStateFlow(FormUiState())
    // Public read-only state flow
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    // Items exposed to UI (as Flow from domain use case)
    val items: Flow<List<Transaction>> = getAllItemsUseCase()

    /**
     * Delete an item by id
     */
    fun deleteItem(id: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val deleted = deleteItemUseCase(id)
                println("Deleted rows: $deleted")
            } catch (t: Throwable) {
                println("Error deleting item: ${t.message}")
            }
        }
    }

    /**
     * Called when the transaction type changes.
     */
    fun onTransactionTypeChange(newType: DomainTransactionType) {
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
    fun submit(month: Int, year: Int) {
        // Validate required fields: category and quantity
        val state = uiState.value
        val hasValidQuantity = state.quantity.isNotBlank() && !state.isQuantityError
        val hasCategory = if (state.transactionType == DomainTransactionType.EXPENSE) state.expenseCategory != null
        else state.incomeCategory != null

        if (!hasValidQuantity || !hasCategory) {
            // If quantity invalid or missing, mark quantity error if applicable
            _uiState.update { currentState ->
                currentState.copy(isQuantityError = currentState.quantity.isNotBlank() && currentState.isQuantityError)
            }
            return
        }

        // Build item from uiState
        // Persist an actual occurrence date (epoch millis) for the selected month.
        val (monthStartMillis, _) = monthBoundsUtcMillis(month = month, year = year)

        // Keep the human-readable date string in the description for now (optional)
        val dateText = state.date.ifBlank {
            "$year-${month.toString().padStart(2, '0')}-01"
        }

        // parse quantity into cents
        val amountDouble = state.quantity.replace(",", ".").toDoubleOrNull() ?: 0.0
        val amountCents = round(amountDouble * 100).toLong()

        val item = Transaction(
            amountCents = amountCents,
            type = state.transactionType,
            description = "${state.notes} | date: $dateText",
            date = monthStartMillis,
            isRecurring = state.isRecurring,
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
