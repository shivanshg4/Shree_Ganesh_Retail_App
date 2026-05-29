package com.example.shreeganesh.ui.screens.dashboard

import com.example.shreeganesh.domain.models.DashboardMetrics

data class DashboardUiState(
    val metrics: DashboardMetrics = DashboardMetrics(0.0, 0.0, 0, 0),
    val isAdmin: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
