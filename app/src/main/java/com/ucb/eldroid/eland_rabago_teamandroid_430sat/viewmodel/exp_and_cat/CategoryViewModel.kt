package com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.exp_and_cat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.model.CategoryItem

class CategoryViewModel(private val dbHelper: AppDatabaseHelper) : ViewModel() {

    private val _categories = MutableLiveData<List<CategoryItem>>()
    val categories: LiveData<List<CategoryItem>> = _categories

    private val _insertSuccess = MutableLiveData<Boolean>()
    val insertSuccess: LiveData<Boolean> = _insertSuccess

    private var userId: Int? = null

    fun loadUser(email: String) {
        val user = dbHelper.getUserByEmail(email)
        userId = user?.userID
        loadCategories()
    }

    private fun loadCategories() {
        val uid = userId ?: return
        val rawList = dbHelper.getCategoriesByUserId(uid)
        val formatted = rawList.map {
            CategoryItem(
                itemID = it["itemID"]?.toIntOrNull() ?: 0,
                itemName = it["itemName"] ?: "",
                itemDescription = it["itemDescription"] ?: ""
            )
        }
        _categories.value = formatted
    }

    fun addCategory(name: String, desc: String) {
        val uid = userId
        if (uid != null && name.isNotBlank() && desc.isNotBlank()) {
            val success = dbHelper.insertCategory(name, desc, uid)
            _insertSuccess.value = success
            if (success) loadCategories()
        } else {
            _insertSuccess.value = false
        }
    }
}