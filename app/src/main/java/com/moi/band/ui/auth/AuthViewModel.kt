package com.moi.band.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moi.band.data.model.AuthResponse
import com.moi.band.data.repository.AuthRepository
import com.moi.band.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 🔐 AuthViewModel - логика авторизации и регистрации
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    // ==================== LOGIN STATE ====================
    
    private val _loginState = MutableStateFlow<Resource<AuthResponse>?>(null)
    val loginState: StateFlow<Resource<AuthResponse>?> = _loginState.asStateFlow()
    
    /**
     * Логин пользователя
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            authRepository.login(username, password).collect { result ->
                _loginState.value = result
            }
        }
    }
    
    /**
     * Сбросить состояние логина
     */
    fun resetLoginState() {
        _loginState.value = null
    }
    
    // ==================== REGISTER STATE ====================
    
    private val _registerState = MutableStateFlow<Resource<AuthResponse>?>(null)
    val registerState: StateFlow<Resource<AuthResponse>?> = _registerState.asStateFlow()
    
    /**
     * Регистрация пользователя
     */
    fun register(
        username: String,
        email: String,
        password: String,
        passwordConfirm: String
    ) {
        viewModelScope.launch {
            authRepository.register(
                username = username,
                email = email,
                password = password,
                passwordConfirm = passwordConfirm
            ).collect { result ->
                _registerState.value = result
            }
        }
    }
    
    /**
     * Сбросить состояние регистрации
     */
    fun resetRegisterState() {
        _registerState.value = null
    }
    
    // ==================== AUTH CHECK ====================
    
    /**
     * Проверить, авторизован ли пользователь
     */
    suspend fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
}
