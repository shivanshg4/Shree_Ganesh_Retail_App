package com.example.shreeganesh.ui.screens.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shreeganesh.data.repository.AuthRepository
import com.example.shreeganesh.domain.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserManagementUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<UserManagementUiState> = authRepository.allUsers.map { users ->
        UserManagementUiState(users = users)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserManagementUiState(isLoading = true)
    )

    fun addUser(name: String, role: String, pin: String) {
        viewModelScope.launch {
            authRepository.addUser(name, role, pin)
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            authRepository.deleteUser(userId)
        }
    }
}
