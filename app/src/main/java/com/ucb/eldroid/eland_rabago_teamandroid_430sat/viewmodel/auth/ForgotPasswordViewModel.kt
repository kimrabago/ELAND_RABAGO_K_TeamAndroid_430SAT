package com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dbHelper = AppDatabaseHelper(application)
    private val app = getApplication<Application>()

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank()) {
            onError(app.getString(R.string.enter_email))
            return
        }

        viewModelScope.launch {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val updated = dbHelper.updateVerificationStatus(email, false)
                        if (updated > 0) {
                            // Log update if needed
                        }
                        onSuccess()
                    } else {
                        val reason = task.exception?.message ?: app.getString(R.string.error_unknown)
                        val errorMessage = app.getString(R.string.reset_failed, reason)
                        onError(errorMessage)
                    }
                }
        }
    }
}