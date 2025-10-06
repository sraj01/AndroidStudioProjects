package com.example.tasklyy.Local.Preference

import android.content.Context
import javax.inject.Inject
import androidx.core.content.edit


class SharedPrefsHelper @Inject constructor(context: Context) {
        private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        fun saveLoggedInUser(username: String) {
            prefs.edit { putString("logged_in_user", username) }
        }

        fun getLoggedInUser(): String? {
            return prefs.getString("logged_in_user", null)
        }

        fun clearUser() {
            prefs.edit { remove("logged_in_user") }
        }
    }

