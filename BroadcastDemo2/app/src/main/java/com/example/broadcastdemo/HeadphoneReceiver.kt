package com.example.broadcastdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class HeadphoneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_HEADSET_PLUG) {
            val state = intent.getIntExtra("state", -1)
            when (state) {
                0 -> Toast.makeText(context, "Headphones Unplugged", Toast.LENGTH_SHORT).show()
                1 -> Toast.makeText(context, "Headphones Plugged", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(context, "Unknown Headphone State", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
