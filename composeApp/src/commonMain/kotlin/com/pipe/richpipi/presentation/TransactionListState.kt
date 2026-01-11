package com.pipe.richpipi.presentation

import com.pipe.richpipi.domain.model.Transaction

data class TransactionListState(
    val transactions: List<Transaction> = emptyList()
)