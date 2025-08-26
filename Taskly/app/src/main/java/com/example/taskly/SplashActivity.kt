package com.example.tasklyy

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.taskly.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Fix Android 12 flicker issue
        setTheme(com.example.taskly.R.style.Theme_YourApp_NoActionBar)

        // ✅ Setup View Binding
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Observe LiveData for navigation
        splashViewModel.navigateToMain.observe(this) { navigate ->
            if (navigate) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}
