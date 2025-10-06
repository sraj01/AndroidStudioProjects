package com.example.tasklyy.Utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.tasklyy.AuthenticationScreens.AuthRepository
import com.example.tasklyy.AuthenticationScreens.LoginScreen.LoginActivity
import com.example.tasklyy.Local.DB.AppDatabase
import com.example.tasklyy.Local.DB.UserEntity
import com.example.tasklyy.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.let

class GoogleSignInUtils {


    companion object {

        fun doGoogleSignUP(
            context: Context,
            scope: CoroutineScope,
            isLogin: Boolean,
            launcher: ActivityResultLauncher<Intent>,
            login: () -> Unit
        ) {
            val credentialManager = CredentialManager.Companion.create(context)
            lateinit var repository: AuthRepository

            val db = AppDatabase.getInstance(context)
            val userDao = db.userDao


            val request = GetCredentialRequest.Builder()
                .addCredentialOption(getCredentialOptions(context))
                .build()

            scope.launch {
                try {
                    val result = credentialManager.getCredential(context, request)
                    when (result.credential) {
                        is CustomCredential -> {
                            Log.d(
                                "GoogleSignInUtils",
                                "doGoogleSignIn: get credential data: " + result.credential
                            )
                            if (result.credential.type == GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val googleIdTokenCredential: GoogleIdTokenCredential =
                                    GoogleIdTokenCredential.Companion.createFrom(result.credential.data)
                                val email: String? = googleIdTokenCredential.id

                                val googleTokenId = googleIdTokenCredential.idToken
                                val authCredential =
                                    GoogleAuthProvider.getCredential(googleTokenId, null)
                                Log.d("GoogleSignInUtils", "doGoogleSignIn: email: $email")

                                // TODO: validate for exixting emailid before  firebase login

                                val existingUser = userDao.getUserfromEmail(email ?: "")

                                if (isLogin) {
                                    if (existingUser != null) {
                                        val user =
                                            Firebase.auth.signInWithCredential(authCredential)
                                                .await().user
                                        user?.let {
                                            if (!it.isAnonymous) {
                                                login.invoke()
                                            }
                                        }
                                    } else {
                                        showToast(
                                            context,
                                            "No account found. Please sign up first."
                                        )
                                    }

                                } else {
                                    if (existingUser != null) {
                                        showToast(context, "Email id already exist. Please login.")
                                    } else {
                                        userDao.insertUser(UserEntity(emailId = email ?: ""))
                                        val user =
                                            Firebase.auth.signInWithCredential(authCredential)
                                                .await().user
                                        user?.let {
                                            if (!it.isAnonymous) {
                                                login.invoke()
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        else -> {}
                    }
                } catch (e: NoCredentialException) {
                    launcher.launch(getIntent())
                } catch (e: GetCredentialException) {
                    e.printStackTrace()
                }
            }
        }

        fun signOutUser(context: Context) {
            val credentialManager = CredentialManager.Companion.create(context)

            Firebase.auth.signOut()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())

                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Signed out successfully!", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                } catch (e: Exception) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            context,
                            "Sign-out failed: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        private fun getIntent(): Intent {
            return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            }
        }

        private fun getCredentialOptions(context: Context): CredentialOption {
            return GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(context.getString(R.string.web_id))
                .build()
        }

        private fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }


    }

}