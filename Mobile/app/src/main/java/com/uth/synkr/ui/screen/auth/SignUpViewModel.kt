package com.uth.synkr.ui.screen.auth

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.synkr.data.model.User
import com.uth.synkr.data.service.UserService
import com.uth.synkr.firebase.auth.AuthResponse
import com.uth.synkr.firebase.auth.FirebaseAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpViewModel(
    private val authManager: FirebaseAuthManager,
    private val userService: UserService = UserService()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()
    
    val isFormValid by derivedStateOf {
        with(_uiState.value) {
            name.isNotBlank() &&
            email.isNotBlank() &&
            phone.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            password == confirmPassword &&
            password.length >= 8 &&
            password.any { it.isDigit() } &&
            password != password.lowercase()
        }
    }
    
    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }
    
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }
    
    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone)
    }
    
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }
    
    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }
    
    fun updatePasswordVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(passwordVisible = visible)
    }
    
    fun updateConfirmPasswordVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(confirmPasswordVisible = visible)
    }
    
    fun signUp(onSuccess: () -> Unit) {
        if (!isFormValid) {
            _uiState.value = _uiState.value.copy(error = "Please fill all fields correctly")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a valid email address")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authManager.createAccountWithEmail(_uiState.value.email, _uiState.value.password)
                .collect { response ->
                    when (response) {
                        is AuthResponse.Success -> {
                            val firebaseUser = Firebase.auth.currentUser
                            if (firebaseUser != null) {
                                val user = User(
                                    uid = firebaseUser.uid,
                                    fullName = _uiState.value.name,
                                    email = _uiState.value.email,
                                    phoneNumber = _uiState.value.phone,
                                    pictureUrl = ""
                                )
                                try {
                                    userService.createUser(user)
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        error = null
                                    )
                                    onSuccess()
                                } catch (e: Exception) {
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        error = "Failed to create user profile: ${e.message}"
                                    )
                                }
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = "Failed to get Firebase user after signup"
                                )
                            }
                        }
                        is AuthResponse.Error -> {
                            val errorMsg = if (response.message.contains("email address is already in use", ignoreCase = true)) {
                                "This email is already registered. Please use another email or sign in."
                            } else response.message
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = errorMsg
                            )
                        }
                    }
                }
        }
    }
    
    fun signUpWithGoogle(onSuccess: () -> Unit) {
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
    
    fun signUpWithFacebook(onSuccess: () -> Unit) {
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

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
