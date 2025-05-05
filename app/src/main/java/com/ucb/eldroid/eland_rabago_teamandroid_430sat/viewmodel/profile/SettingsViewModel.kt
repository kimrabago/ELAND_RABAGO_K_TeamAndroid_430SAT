package com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val dbHelper = AppDatabaseHelper(application.applicationContext)

    private val _deletionSuccess = MutableLiveData<Boolean>()
    val deletionSuccess: LiveData<Boolean> = _deletionSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun deleteUser(getString: (Int) -> String, getStringWithArg: (Int, String) -> String) {
        val user = auth.currentUser
        val email = user?.email ?: return

        val userData = dbHelper.getUserByEmail(email)
        val password = userData?.password ?: run {
            _errorMessage.value = getString(getStringRes("reauthentication_failed"))
            return
        }

        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                user.delete().addOnCompleteListener { deleteTask ->
                    if (deleteTask.isSuccessful) {
                        val rows = dbHelper.deleteUserByEmail(email)
                        if (rows > 0) {
                            _deletionSuccess.value = true
                        }
                    }
                }
            } else {
                val error = authTask.exception?.message ?: getString(getStringRes("error_unknown"))
                _errorMessage.value = getStringWithArg(getStringRes("reauthentication_failed"), error)
            }
        }
    }

    // Helper function for consistent getString ID use
    private fun getStringRes(name: String): Int {
        return when (name) {
            "reauthentication_failed" -> com.ucb.eldroid.eland_rabago_teamandroid_430sat.R.string.reauthentication_failed
            "error_unknown" -> com.ucb.eldroid.eland_rabago_teamandroid_430sat.R.string.error_unknown
            else -> 0
        }
    }
}