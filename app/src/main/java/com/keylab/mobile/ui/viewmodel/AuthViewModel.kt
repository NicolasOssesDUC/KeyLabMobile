package com.keylab.mobile.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.keylab.mobile.data.remote.ApiResponse
import com.keylab.mobile.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableLiveData<ApiResponse<Unit>>()
    val loginState: LiveData<ApiResponse<Unit>> = _loginState

    private val _recoveryState = MutableLiveData<ApiResponse<Unit>>()
    val recoveryState: LiveData<ApiResponse<Unit>> = _recoveryState

    private val _registerState = MutableLiveData<ApiResponse<Unit>>()
    val registerState: LiveData<ApiResponse<Unit>> = _registerState

    fun login(email: String, password: String) {
        _loginState.value = ApiResponse.Loading
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginState.value = result
        }
    }

    fun loginWithGoogle(idToken: String) {
        _loginState.value = ApiResponse.Loading
        viewModelScope.launch {
            val result = repository.loginWithGoogle(idToken)
            _loginState.value = result
        }
    }

    fun recoverPassword(email: String) {
        _recoveryState.value = ApiResponse.Loading
        viewModelScope.launch {
            val result = repository.recoverPassword(email)
            _recoveryState.value = result
        }
    }

    fun signUp(nombre: String, email: String, password: String) {
        _registerState.value = ApiResponse.Loading
        viewModelScope.launch {
            val result = repository.signUp(nombre, email, password)
            _registerState.value = result
        }
    }
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
