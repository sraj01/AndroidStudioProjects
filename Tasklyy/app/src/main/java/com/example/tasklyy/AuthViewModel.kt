package com.example.tasklyy


import androidx.lifecycle.*
import com.example.tasklyy.AuthRepository
import com.example.tasklyy.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.rpc.context.AttributeContext
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableLiveData<AttributeContext.Resource<Boolean>>()
    val loginState: LiveData<AttributeContext.Resource<Boolean>> get() = _loginState

    fun loginWithGoogle(account: GoogleSignInAccount) {
        _loginState.value = AttributeContext.Resource.Loading()
        viewModelScope.launch {
            val result = repository.signInWithGoogle(account)
            if (result.isSuccess) {
                _loginState.value = AttributeContext.Resource.Success(true)
            } else {
                _loginState.value = AttributeContext.Resource.Error(result.exceptionOrNull()?.message ?: "Something went wrong")
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return repository.getCurrentUser() != null
    }
}
