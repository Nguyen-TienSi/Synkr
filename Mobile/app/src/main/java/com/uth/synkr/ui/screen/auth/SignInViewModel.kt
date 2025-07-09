package com.uth.synkr.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.synkr.firebase.auth.AuthResponse
import com.uth.synkr.firebase.auth.FirebaseAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val authManager: FirebaseAuthManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()
    
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }
    
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }
    
    fun updateRememberMe(rememberMe: Boolean) {
        _uiState.value = _uiState.value.copy(rememberMe = rememberMe)
    }
    
    fun signIn(onSuccess: () -> Unit) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a valid email address")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            authManager.signInWithEmail(_uiState.value.email, _uiState.value.password)
                .collect { response ->
                    when (response) {
                        is AuthResponse.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = null
                            )
                            onSuccess()
                        }
                        is AuthResponse.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = response.message
                            )
                        }
                    }
                }
        }
    }
    
    fun signInWithGoogle(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            authManager.signInWithGoogle().collect { response ->
                when (response) {
                    is AuthResponse.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                        onSuccess()
                    }
                    is AuthResponse.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = response.message
                        )
                    }
                }
            }
        }
    }
    
    fun signInWithFacebook(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            authManager.signInWithFacebook().collect { response ->
                when (response) {
                    is AuthResponse.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                        onSuccess()
                    }
                    is AuthResponse.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = response.message
                        )
                    }
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
