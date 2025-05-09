package com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.budgetplan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper

class BudgetPlanningViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = AppDatabaseHelper(application)

    private val _totalBudget = MutableLiveData<Int>()
    val totalBudget: LiveData<Int> = _totalBudget

    private val _categories = MutableLiveData<List<Map<String, String>>>()
    val categories: LiveData<List<Map<String, String>>> = _categories

    private val _budgetLimit = MutableLiveData<Int>()
    val budgetLimit: LiveData<Int> = _budgetLimit

    private val _totalExpense = MutableLiveData<Int>()
    val totalExpense: LiveData<Int> = _totalExpense

    private val _warningMessageVisibility = MutableLiveData<Boolean>()
    val warningMessageVisibility: LiveData<Boolean> = _warningMessageVisibility

    private var userID: Int? = null
    private val _selectedCategoryID = MutableLiveData<Int?>()
    val selectedCategoryID: LiveData<Int?> = _selectedCategoryID

    fun initializeUser(userEmail: String?) {
        val user = userEmail?.let { dbHelper.getUserByEmail(it) }
        userID = user?.userID
        loadCategories()
        loadTotalBudget()
    }

    private fun loadCategories() {
        userID?.let {
            _categories.value = dbHelper.getCategoriesByUserId(it)
        }
    }

    private fun loadTotalBudget() {
        userID?.let {
            _totalBudget.value = dbHelper.getTotalBudgetForUser(it)
        }
    }

    fun onCategorySelected(categoryId: Int) {
        _selectedCategoryID.value = categoryId
        loadCategoryBudgetAndExpenses()
    }

    private fun loadCategoryBudgetAndExpenses() {
        val categoryId = _selectedCategoryID.value ?: return
        val uid = userID ?: return

        val budget = dbHelper.getTotalBudgetForItem(uid, categoryId)
        _budgetLimit.value = budget

        val expenses = dbHelper.getExpensesByItemId(uid, categoryId)
        val totalExpenseAmount = expenses.sumOf { it["expenseAmount"]?.toIntOrNull() ?: 0 }
        _totalExpense.value = totalExpenseAmount

        _warningMessageVisibility.value = totalExpenseAmount > budget
    }

    fun updateBudget(newLimit: Int) {
        val uid = userID ?: return
        val categoryId = _selectedCategoryID.value ?: return

        val existingBudgetId = dbHelper.getBudgetId(uid, categoryId)
        if (existingBudgetId != null) {
            dbHelper.updateBudgetLimit(existingBudgetId, newLimit)
        } else {
            dbHelper.insertBudget(newLimit, uid, categoryId)
        }

        loadCategoryBudgetAndExpenses()
        loadTotalBudget()
    }
}