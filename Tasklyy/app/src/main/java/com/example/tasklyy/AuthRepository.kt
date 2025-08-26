
package com.example.tasklyy

import com.example.tasklyy.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    // Sign in using Google and store user data in Firestore
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<Boolean> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()

            // Get Firebase user UID
            val userId = authResult.user?.uid ?: return Result.failure(Exception("User ID not found"))

            // Create user object
            val user = User(
                uid = userId,
                name = account.displayName ?: "",
                email = account.email ?: "",

            )

            // Save user data in Firestore
            firestore.collection("users").document(userId).set(user).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser() = firebaseAuth.currentUser
}
