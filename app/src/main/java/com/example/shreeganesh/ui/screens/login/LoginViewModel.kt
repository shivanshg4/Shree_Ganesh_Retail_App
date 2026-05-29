package com.example.shreeganesh.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shreeganesh.data.repository.AuthRepository
import com.example.shreeganesh.data.repository.POSRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val pin: String = "",
    val error: String? = null,
    val storeName: String = "Shree Ganesh",
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val posRepository: POSRepository
) : ViewModel() {

    private val _pin = MutableStateFlow("")
    private val _error = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isAuthenticated = MutableStateFlow(false)

    val uiState: StateFlow<LoginUiState> = combine(
        _pin, _error, _isLoading, _isAuthenticated, posRepository.storeSettings
    ) { pin, error, loading, authenticated, settings ->
        LoginUiState(
            pin = pin,
            error = error,
            storeName = settings?.storeName ?: "Shree Ganesh",
            isLoading = loading,
            isAuthenticated = authenticated
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoginUiState()
    )

    fun onNumberClick(number: String) {
        if (_pin.value.length < 4) {
            _pin.value += number
            _error.value = null
            
            if (_pin.value.length == 4) {
                attemptLogin()
            }
        }
    }

    fun onDeleteClick() {
        if (_pin.value.isNotEmpty()) {
            _pin.value = _pin.value.dropLast(1)
            _error.value = null
        }
    }

    private fun attemptLogin() {
        viewModelScope.launch {
            _isLoading.value = true
            val success = authRepository.login(_pin.value)
            _isLoading.value = false
            
            if (success) {
                _isAuthenticated.value = true
            } else {
                _pin.value = ""
                _error.value = "Invalid PIN. Please try again."
            }
        }
    }
}
