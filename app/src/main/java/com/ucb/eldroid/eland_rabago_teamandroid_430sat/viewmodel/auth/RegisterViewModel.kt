package com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper
import com.ucb.eldroid.eland_teamandroid_430sat.data.model.UserData

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val dbHelper = AppDatabaseHelper(application.applicationContext)

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun registerUser(
        username: String,
        email: String,
        password: String,
        getString: (Int) -> String,
        getStringWithArg: (Int, String) -> String
    ) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _errorMessage.value = getString(com.ucb.eldroid.eland_rabago_teamandroid_430sat.R.string.fill_both_fields)
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                val userData = UserData(
                                    userID = 0,
                                    username = username,
                                    email = email,
                                    password = password,
                                    isVerified = false
                                )

                                val rowId = dbHelper.insertUser(userData)
                                if (rowId != -1L) {
                                    _registrationSuccess.value = true
                                } else {
                                    _errorMessage.value = getString(com.ucb.eldroid.eland_rabago_teamandroid_430sat.R.string.sqlite_save_failed)
                                }
                            } else {
                                _errorMessage.value = getString(com.ucb.eldroid.eland_rabago_teamandroid_430sat.R.string.verification_email_failed)
                            }
                        }
                } else {
                    _errorMessage.value = getStringWithArg(
                        com.ucb.eldroid.eland_rabago_teamandroid_430sat.R.string.registration_failed,
                        task.exception?.message ?: "Unknown error"
                    )
                }
            }
    }
}