package com.example.shreeganesh.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shreeganesh.data.repository.AuthRepository
import com.example.shreeganesh.data.repository.POSRepository
import com.example.shreeganesh.domain.models.StoreSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: POSRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        repository.storeSettings,
        authRepository.currentUser
    ) { settings, user ->
        SettingsUiState(
            settings = settings ?: StoreSettings("Shree Ganesh", "Main St", 8.0, "$"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(isLoading = true)
    )

    fun updateStoreName(name: String) {
        viewModelScope.launch {
            repository.updateSettings(uiState.value.settings.copy(storeName = name))
        }
    }

    fun updateStoreAddress(address: String) {
        viewModelScope.launch {
            repository.updateSettings(uiState.value.settings.copy(storeAddress = address))
        }
    }

    fun updateTaxRate(rate: Double) {
        viewModelScope.launch {
            repository.updateSettings(uiState.value.settings.copy(taxRate = rate))
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        // Implement logic
    }
}
