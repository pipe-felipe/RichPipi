package com.pipe.richpipi.form

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Represents the state of the form UI.
 */
data class FormUiState(
    val textValue: String = ""
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
     * Called when the text field value changes.
     */
    fun onTextChange(newText: String) {
        _uiState.update { currentState ->
            currentState.copy(textValue = newText)
        }
    }

    /**
     * Called when the form is submitted.
     */
    fun submit() {
        // You can add your submission logic here.
        // For example, print the value:
        println("Submitted value: ${_uiState.value.textValue}")

        // You might want to reset the state after submission
        // _uiState.value = FormUiState()
    }
}
