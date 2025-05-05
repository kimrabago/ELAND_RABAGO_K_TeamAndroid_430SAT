package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.data.locals.AppDatabaseHelper
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.auth.RegisterViewModel
import com.ucb.eldroid.eland_teamandroid_430sat.data.model.UserData

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var registerBtn: Button

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        FirebaseApp.initializeApp(this)

        // Bind UI elements
        usernameET = findViewById(R.id.etUserName)
        emailET = findViewById(R.id.etEmail)
        passwordET = findViewById(R.id.etPassword)
        registerBtn = findViewById(R.id.registerBtn)

        registerBtn.setOnClickListener {
            val username = usernameET.text.toString().trim()
            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString().trim()

            // Trigger registration logic in ViewModel
            viewModel.registerUser(
                username = username,
                email = email,
                password = password,
                getString = { resId -> getString(resId) },
                getStringWithArg = { resId, arg -> getString(resId, arg) }
            )
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        // Observe success
        viewModel.registrationSuccess.observe(this, Observer { success ->
            if (success) {
                startActivity(Intent(this, EmailVerificationActivity::class.java))
                finish()
            }
        })

        // Observe error messages
        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }
}
