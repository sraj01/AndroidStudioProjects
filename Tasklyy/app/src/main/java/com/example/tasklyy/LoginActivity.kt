package com.example.tasklyy

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.tasklyy.GoogleSignInUtils
import com.example.tasklyy.MainActivity
import com.example.tasklyy.databinding.ActivityLoginBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val scope = MainScope()

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        doGoogleSignIn()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoogle.setOnClickListener {
            doGoogleSignIn()
        }
    }

    private fun doGoogleSignIn() {
        GoogleSignInUtils.doGoogleSignIn(
            context = this,
            scope = scope,
            launcher = launcher,
            login = {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        scope.cancel()
    }
}
