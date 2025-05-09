package com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.exp_and_cat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper

class ExpenseViewModel(private val dbHelper: AppDatabaseHelper) : ViewModel() {

    private val _categoryList = MutableLiveData<List<Map<String, String>>>()
    val categoryList: LiveData<List<Map<String, String>>> = _categoryList

    private val _expenseSaved = MutableLiveData<Boolean>()
    val expenseSaved: LiveData<Boolean> = _expenseSaved

    var userId: Int? = null
    var selectedCategoryId: Int? = null

    fun loadCategories(email: String) {
        val user = dbHelper.getUserByEmail(email)
        userId = user?.userID
        if (userId != null) {
            _categoryList.value = dbHelper.getCategoriesByUserId(userId!!)
        }
    }

    fun submitExpense(name: String, amountText: String, desc: String) {
        val amount = amountText.toIntOrNull()
        val uid = userId
        val itemId = selectedCategoryId

        _expenseSaved.value = if (
            name.isNotBlank() && amount != null && uid != null && itemId != null
        ) {
            dbHelper.insertExpense(name, amount, desc, uid, itemId)
        } else {
            false
        }
    }
}