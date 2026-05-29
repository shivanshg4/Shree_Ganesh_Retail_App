package com.example.shreeganesh.ui.screens.settings

import com.example.shreeganesh.domain.models.StoreSettings

data class SettingsUiState(
    val settings: StoreSettings = StoreSettings("", "", 0.0, "$"),
    val isDarkMode: Boolean = false,
    val isAdmin: Boolean = false,
    val isLoading: Boolean = false
)
