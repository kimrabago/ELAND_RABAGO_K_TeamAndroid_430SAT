package com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.bottom_nav

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.model.CategoryItem

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = AppDatabaseHelper(application)

    private val _categories = MutableLiveData<List<CategoryItem>>()
    val categories: LiveData<List<CategoryItem>> = _categories

    private val _expenseMap = MutableLiveData<Map<Int, Int>>() // itemID -> total
    val expenseMap: LiveData<Map<Int, Int>> = _expenseMap

    private val _totalExpenses = MutableLiveData<Int>()
    val totalExpenses: LiveData<Int> = _totalExpenses

    private val _userId = MutableLiveData<Int>()

    fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        val userData = user?.email?.let { dbHelper.getUserByEmail(it) } ?: return
        _userId.value = userData.userID

        val categoryList = dbHelper.getCategoriesByUserId(userData.userID).map {
            CategoryItem(
                itemID = it["itemID"]?.toIntOrNull() ?: 0,
                itemName = it["itemName"] ?: "",
                itemDescription = it["itemDescription"] ?: ""
            )
        }

        val expenseData = dbHelper.getTotalExpensesPerItem(userData.userID)
        val total = expenseData.values.sum()

        _categories.value = categoryList
        _expenseMap.value = expenseData
        _totalExpenses.value = total
    }

    fun getExpensesByCategory(itemId: Int): List<Map<String, String>> {
        return dbHelper.getExpensesByItemId(_userId.value ?: return emptyList(), itemId)
    }

    fun deleteExpense(
        expenseName: String,
        expenseAmount: String,
        itemId: Int
    ): Boolean {
        return dbHelper.deleteExpense(
            expenseName,
            expenseAmount,
            _userId.value ?: return false,
            itemId
        )
    }

    fun getUserId(): Int? = _userId.value

    fun getCategoryIdByName(name: String): Int {
        val userId = _userId.value ?: return -1
        return dbHelper.getCategoryIdByName(userId, name)
    }
}