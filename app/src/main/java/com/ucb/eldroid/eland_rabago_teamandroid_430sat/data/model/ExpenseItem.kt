package com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.model

data class ExpenseItem(
    val expenseID: Int,
    val expenseName: String,
    val expenseAmount: Int,
    val expenseDescription: String?,
    val userID: Int,
    val itemID: Int
)