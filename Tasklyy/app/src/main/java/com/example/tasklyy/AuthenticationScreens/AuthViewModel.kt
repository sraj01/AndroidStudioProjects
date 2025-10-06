package com.example.tasklyy.AuthenticationScreens

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableLiveData<Boolean>()
    val authState: LiveData<Boolean> = _authState

    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> get() = _loginState


    fun signUpWithGoogle(
        context: Context,
        launcher: ActivityResultLauncher<Intent>,
        onLoginSuccess: () -> Unit
    ) {
        repository.signUpWithGoogle(
            context = context,
            scope = MainScope(),
            launcher = launcher,
            onLoginSuccess = onLoginSuccess
        )
    }
    fun  signInWithGoogle(
        context: Context,
        launcher: ActivityResultLauncher<Intent>,
        onLoginSuccess: () -> Unit
    ) {
        repository.signInWithGoogle(
            context = context,
            scope = MainScope(),
            launcher = launcher,
            onLoginSuccess = onLoginSuccess
        )
    }

    fun signUp(
        username: String,
        password: String,
        confirmPassword: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.signUp(username, password,scope = viewModelScope, email = "") { success, message ->
                _authState.postValue(success)
                onResult(success, message)
            }
        }
    }
    fun login(
        username: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        repository.login(username, password, viewModelScope) { success, message ->
            _loginState.postValue(success)
            onResult(success, message)
        }
    }
}