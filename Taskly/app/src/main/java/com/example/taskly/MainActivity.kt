package com.example.taskly

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.taskly.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            Log.d("Taskly", "Logged in as: ${auth.currentUser?.email}")
        } else {
            Log.d("Taskly", "No user logged in")
        }



        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
