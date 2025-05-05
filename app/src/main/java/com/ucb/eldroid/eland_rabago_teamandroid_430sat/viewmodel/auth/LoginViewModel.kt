package com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.auth

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val dbHelper = AppDatabaseHelper(application.applicationContext)
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage.value = context.getString(R.string.email_password_required)
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        val updated = dbHelper.updateVerificationStatus(email, true)
                        if (updated <= 0) {
                            _errorMessage.value = context.getString(R.string.update_verification_failed)
                        }
                        _loginSuccess.value = true
                    } else {
                        _errorMessage.value = context.getString(R.string.please_verify_email)
                        auth.signOut()
                    }
                } else {
                    _errorMessage.value = context.getString(
                        R.string.login_failed,
                        task.exception?.message ?: context.getString(R.string.error_unknown)
                    )
                }
            }
    }

    fun isUserAlreadyLoggedIn(): Boolean {
        val user = auth.currentUser
        return user != null && user.isEmailVerified
    }
}