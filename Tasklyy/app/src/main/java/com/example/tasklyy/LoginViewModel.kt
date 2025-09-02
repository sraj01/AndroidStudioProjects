package com.example.tasklyy.viewmodel

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasklyy.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> get() = _loginState

    private val scope = MainScope()

    fun signInWithGoogle(
        context: Context,
        launcher: ActivityResultLauncher<Intent>
    ): Unit {
        repository.signInWithGoogle(context, scope, launcher) {
            _loginState.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}
