package com.example.tasklyy.AuthenticationScreens.SignUpScreen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tasklyy.AuthenticationScreens.LoginScreen.LoginActivity
import com.example.tasklyy.AuthenticationScreens.LoginViewModel
import com.example.tasklyy.HomeScreen.MainActivity
import com.example.tasklyy.databinding.ActivitySignupBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val viewModel: LoginViewModel by viewModels()

    private val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Toast.makeText(this, "Google Sign-In Completed", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        observeViewModel()
        handleClicks()
    }

    private fun initUI() {
        binding.progressBar.visibility = View.GONE
    }

    private fun observeViewModel() {
        viewModel.authState.observe(this) { success ->
            if (success) navigateToMain()
        }

        viewModel.loginState.observe(this) { success ->
            binding.progressBar.visibility = View.GONE
            if (success) {
                showToast("Account created successfully!")
                navigateToMain()
            } else {
                showToast("Sign in failed")
            }
        }
    }

    private fun handleClicks() {
        // Normal Signup
        binding.btnSignUp.setOnClickListener {
            val username = binding.et2Email.text.toString().trim()
            val password = binding.et2Password.text.toString().trim()
            val confirmPassword = binding.et3Password.text.toString().trim()

            if (!validateSignUpInputs(
                    username,
                    password,
                    confirmPassword
                )
            ) return@setOnClickListener
            // TODO: validations of signup fields create and  specific method for validations

            binding.progressBar.visibility = View.VISIBLE

            viewModel.signUp(username, password, confirmPassword) { success, message ->
                binding.progressBar.visibility = View.GONE
                showToast(message)

            }
        }

        binding.btnGoogle.setOnClickListener {
            viewModel.signUpWithGoogle(
                context = this,
                launcher = launcher,
                onLoginSuccess = {
                    Toast.makeText(this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
            )
        }
        binding.btnBackToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    private fun validateSignUpInputs(
        username: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        return when {
            username.isEmpty() -> {
                showToast("Please enter username")
                false
            }

            password.isEmpty() -> {
                showToast("Please enter password")
                false
            }

            confirmPassword.isEmpty() -> {
                showToast("Please enter password")
                false
            }

            password != confirmPassword -> {

                showToast("Password should match Confirm Password")
                false
            }

            else -> {
                true
            }
        }

    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}