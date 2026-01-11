package com.pipe.richpipi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pipe.richpipi.domain.usecase.GetAllTransactionsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TransactionListViewModel(getAllTransactionsUseCase: GetAllTransactionsUseCase) : ViewModel() {

    val state: StateFlow<TransactionListState> = getAllTransactionsUseCase()
        .map { TransactionListState(transactions = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = TransactionListState()
        )
}