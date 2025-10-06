package com.example.tasklyy.AuthenticationScreens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.example.tasklyy.Local.DB.UserDao
import com.example.tasklyy.Local.DB.UserEntity
import com.example.tasklyy.Local.Preference.SharedPrefsHelper
import com.example.tasklyy.Utils.GoogleSignInUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    /*
        private val firebaseAuth: FirebaseAuth,
    */
    private val userDao: UserDao,

    private val sharedPrefs: SharedPrefsHelper
) {

    fun signUpWithGoogle(
        context: Context,
        scope: CoroutineScope,
        launcher: ActivityResultLauncher<Intent>,
        onLoginSuccess: () -> Unit
    ) {
        GoogleSignInUtils.Companion.doGoogleSignUP(
            context = context,
            scope = scope,
            launcher = launcher,
            isLogin = false,
            login = onLoginSuccess
        )
    }
    fun signInWithGoogle(
        context: Context,
        scope: CoroutineScope,
        launcher: ActivityResultLauncher<Intent>,
        onLoginSuccess: () -> Unit
    ) {
        GoogleSignInUtils.Companion.doGoogleSignUP(
            context = context,
            scope = scope,
            launcher = launcher,
            isLogin = true,
            login = onLoginSuccess
        )
    }
    fun signUp(
        username: String = "",
        password: String = "",
        email: String = "",
        scope: CoroutineScope,
        onResult: (Boolean, String) -> Unit

    ) {
        // TODO: get user by username or emailid (method change in dao)
        scope.launch(Dispatchers.IO) {
            Log.d("AuthRepository", "username:" + username)
            Log.d("AuthRepository", "password:" + password)
            Log.d("AuthRepository", "email:" + email)

            val existingUser = userDao.getUserfromUsername(username)


            if (existingUser != null) {
                withContext(Dispatchers.Main) {
                    onResult(false, "User already exists")

                }
                return@launch
            }
            userDao.insertUser(
                UserEntity(
                    userName = username,
                    passWord = password,
                    emailId = email
                )
            )
            withContext(Dispatchers.Main) {
                onResult(true, "Signup successful")
            }
        }
    }

    fun login(
        username: String = "",
        password: String = "",

        scope: CoroutineScope,
        onResult: (Boolean, String) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            if (username.isBlank() || password.isBlank()) {
                withContext(Dispatchers.Main) {
                    onResult(
                        false,
                        "Username/Password cannot be empty"
                    )
                }
                return@launch
            }

            val user = userDao.getUserfromUsername(username)
            if (user != null && user.passWord == password) {
                sharedPrefs.saveLoggedInUser(user.userName)
                withContext(Dispatchers.Main) { onResult(true, "Login successful") }
            } else {
                withContext(Dispatchers.Main) { onResult(false, "Invalid username or password") }
            }
        }
    }
    fun getLoggedInUser(): String? = sharedPrefs.getLoggedInUser()
    fun logout() = sharedPrefs.clearUser()
}

