package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.firebase.FirebaseApp
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.bottom_nav.AppSummaryActivity
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.auth.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FirebaseApp.initializeApp(this)

        val emailET = findViewById<EditText>(R.id.etEmail)
        val passwordET = findViewById<EditText>(R.id.etPassword)
        val loginBtn = findViewById<Button>(R.id.btn_login)
        val forgotPassTxtView = findViewById<TextView>(R.id.forgotPassword)
        val signUpBtn = findViewById<TextView>(R.id.signUpTxtView)

        if (viewModel.isUserAlreadyLoggedIn()) {
            startActivity(Intent(this, AppSummaryActivity::class.java))
            finish()
            return
        }

        forgotPassTxtView.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        signUpBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginBtn.setOnClickListener {
            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString().trim()
            viewModel.login(email, password)
        }

        viewModel.loginSuccess.observe(this, Observer { success ->
            if (success) {
                startActivity(Intent(this, AppSummaryActivity::class.java))
                finish()
            }
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }
}