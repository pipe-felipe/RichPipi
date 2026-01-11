package com.pipe.richpipi.domain.model

data class Transaction(
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val date: Long
)