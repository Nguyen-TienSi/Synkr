package com.uth.synkr.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.synkr.firebase.auth.FirebaseAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authManager: FirebaseAuthManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()
    
    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone)
    }
    
    fun sendCode(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // TODO: Implement forgot password logic
            // For now, just simulate success
            try {
                // Simulate API call
                kotlinx.coroutines.delay(2000)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null,
                    isCodeSent = true
                )
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to send code"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ForgotPasswordUiState(
    val phone: String = "",
    val isLoading: Boolean = false,
    val isCodeSent: Boolean = false,
    val error: String? = null
)
