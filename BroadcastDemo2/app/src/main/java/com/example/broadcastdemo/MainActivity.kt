package com.example.broadcastdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val headphoneReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_HEADSET_PLUG) {
                val state = intent.getIntExtra("state", -1)
                when (state) {
                    0 -> Toast.makeText(context, "Headphones unplugged", Toast.LENGTH_SHORT).show()
                    1 -> Toast.makeText(context, "Headphones plugged", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        registerReceiver(headphoneReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        
        unregisterReceiver(headphoneReceiver)
    }
}
