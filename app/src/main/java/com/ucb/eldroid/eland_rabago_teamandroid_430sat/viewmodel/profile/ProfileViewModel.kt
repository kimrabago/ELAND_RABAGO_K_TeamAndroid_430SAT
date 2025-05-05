package com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper
import com.ucb.eldroid.eland_teamandroid_430sat.data.model.UserData

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = AppDatabaseHelper(application.applicationContext)
    private val auth = FirebaseAuth.getInstance()

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> = _userData

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun loadUserData(email: String) {
        _userData.value = dbHelper.getUserByEmail(email)
    }

    fun updateUserProfile(
        updatedUsername: String,
        updatedEmail: String,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String,
        selectedImageUri: Uri?,
        getString: (Int) -> String
    ) {
        val currentUser = dbHelper.getUserByEmail(updatedEmail)
        if (currentUser == null) {
            _errorMessage.value = getString(com.ucb.eldroid.eland_rabago_teamandroid_430sat.R.string.user_not_found)
            return
        }

        if (newPassword.isNotEmpty() && newPassword != confirmPassword) {
            _errorMessage.value = getString(com.ucb.eldroid.eland_rabago_teamandroid_430sat.R.string.passwords_do_not_match)
            return
        }

        val passwordToUse = when {
            newPassword.isNotEmpty() -> newPassword
            oldPassword.isNotEmpty() -> oldPassword
            else -> currentUser.password
        }

        val updatedUser = UserData(
            userID = currentUser.userID,
            username = updatedUsername,
            email = updatedEmail,
            password = passwordToUse,
            isVerified = currentUser.isVerified,
            profileImage = selectedImageUri?.toString() ?: currentUser.profileImage
        )

        val rowsUpdated = dbHelper.updateUser(updatedUser)
        if (rowsUpdated > 0) {
            if (newPassword.isNotEmpty()) {
                auth.currentUser?.updatePassword(newPassword)
            }
            _updateSuccess.value = true
        } else {
            _errorMessage.value = getString(com.ucb.eldroid.eland_rabago_teamandroid_430sat.R.string.update_failed)
        }
    }
}