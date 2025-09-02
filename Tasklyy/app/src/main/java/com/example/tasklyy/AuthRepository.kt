package com.example.tasklyy.repository

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.tasklyy.GoogleSignInUtils
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope

class AuthRepository @Inject constructor() {

    fun signInWithGoogle(
        context: Context,
        scope: CoroutineScope,
        launcher: ActivityResultLauncher<Intent>,
        onLoginSuccess: () -> Unit
    ) {
        GoogleSignInUtils.doGoogleSignIn(
            context = context,
            scope = scope,
            launcher = launcher,
            login = onLoginSuccess
        )
    }
}
