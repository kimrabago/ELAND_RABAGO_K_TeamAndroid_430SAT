package com.ucb.eldroid.eland_rabago_teamandroid_430sat.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.R
import com.ucb.eldroid.eland_rabago_teamandroid_430sat.viewmodel.auth.ForgotPasswordViewModel

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var emailET: EditText
    private lateinit var resetBtn: Button

    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        emailET = findViewById(R.id.et_email)
        resetBtn = findViewById(R.id.btn_reset_pass)

        resetBtn.setOnClickListener {
            val email = emailET.text.toString().trim()

            viewModel.resetPassword(
                email,
                onSuccess = {
                    Toast.makeText(this, getString(R.string.reset_link_sent, email), Toast.LENGTH_LONG).show()
                },
                onError = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}