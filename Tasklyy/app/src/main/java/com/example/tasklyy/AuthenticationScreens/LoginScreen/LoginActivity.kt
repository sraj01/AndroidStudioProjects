package com.example.tasklyy.AuthenticationScreens.LoginScreen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tasklyy.HomeScreen.MainActivity
import com.example.tasklyy.AuthenticationScreens.SignUpScreen.SignUpActivity
import com.example.tasklyy.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.content.edit
import com.example.tasklyy.AuthenticationScreens.LoginViewModel

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Toast.makeText(this, "Google Sign-In Completed", Toast.LENGTH_SHORT).show()
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.loginState.observe(this) { success ->
            if (success) navigateToMain()
        }

        viewModel.authState.observe(this) { onSuccess ->
            if (onSuccess) navigateToMain()
        }
    }

    private fun setupListeners() {
        binding.tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (!validateLoginInputs(username, password)) return@setOnClickListener

            binding.progressBar.visibility = View.VISIBLE

            viewModel.login(username, password) { success, message ->
                binding.progressBar.visibility = View.GONE
                showToast(message)
                if (success) {
                    saveUserSession(username) // login session
                    navigateToMain()
                }
            }
        }

        binding.btnSignInWithGoogle.setOnClickListener {
            viewModel.signInWithGoogle(context = this, launcher = launcher, onLoginSuccess = {
                Toast.makeText(this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
            )
        }

    }

    private fun validateLoginInputs(username: String, password: String): Boolean {
        return when {
            username.isEmpty() -> {
                showToast("Please enter username")
                false
            }

            password.isEmpty() -> {
                showToast("Please enter password")
                false
            }

            else -> true
        }
    }

    private fun saveUserSession(username: String) {
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        prefs.edit {
            putBoolean("isLoggedIn", true)
                .putString("username", username)
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