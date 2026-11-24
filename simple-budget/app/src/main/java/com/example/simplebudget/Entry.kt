package com.example.simplebudget

data class Entry(
    val id: Long,
    val description: String,
    val amount: Double,
    val isExpense: Boolean,
    val createdAt: Long
)
