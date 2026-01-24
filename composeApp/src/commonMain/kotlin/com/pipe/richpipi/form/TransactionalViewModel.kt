package com.pipe.richpipi.form

import androidx.lifecycle.ViewModel
import com.pipe.richpipi.platform.currentDateString
import domain.model.Transaction
import domain.usecase.DeleteTransactionUseCase
import domain.usecase.GetTransactions
import domain.usecase.MakeTransactionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.round
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import domain.model.TransactionType as DomainTransactionType

enum class ExpenseCategory {
    TRANSPORT, GIFT, RECURRING, FOOD, STUFF, MEDICINE, CLOTHES
}

enum class IncomeCategory {
    SALARY, GIFT, INVESTMENT, OTHER
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
    val isQuantityError: Boolean = false,
)

/**
 * ViewModel to handle the business logic and state of the form.
 */
class TransactionalViewModel(
    private val addItemUseCase: MakeTransactionUseCase,
    getAllItemsUseCase: GetTransactions,
    private val deleteItemUseCase: DeleteTransactionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormUiState())

    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

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
        val isValid = input.replace(",", ".").toFloatOrNull() != null

        _uiState.update { currentState ->
            currentState.copy(
                quantity = input,
                isQuantityError = input.isNotBlank() && !isValid,
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
     * Called when the date field value changes.
     */
    fun onDateChange(newDate: String) {
        _uiState.update { currentState ->
            currentState.copy(date = newDate)
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
        val hasCategory =
            if (state.transactionType == DomainTransactionType.EXPENSE) {
                state.expenseCategory != null
            } else {
                state.incomeCategory != null
            }

        if (!hasValidQuantity || !hasCategory) {
            _uiState.update { currentState ->
                currentState.copy(
                    isQuantityError = currentState.quantity.isNotBlank() && currentState.isQuantityError,
                )
            }
            return
        }

        val amountDouble = state.quantity.replace(",", ".").toDoubleOrNull() ?: 0.0
        val amountCents = round(amountDouble * 100).toLong()

        // Use current date if not provided by user
        val dateText = state.date.ifBlank {
            currentDateString()
        }

        val item = Transaction(
            amountCents = amountCents,
            type = state.transactionType,
            category = (state.expenseCategory ?: state.incomeCategory).toString(),
            description = state.notes,
            humanDate = dateText,
            isRecurring = state.isRecurring,
            createdAt = Clock.System.now().toEpochMilliseconds(),
        )

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
