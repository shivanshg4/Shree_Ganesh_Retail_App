package com.example.shreeganesh.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shreeganesh.data.repository.AuthRepository
import com.example.shreeganesh.data.repository.POSRepository
import com.example.shreeganesh.domain.models.DashboardMetrics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: POSRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.getRevenueForToday(),
        repository.getTransactionCountForToday(),
        repository.getLowStockCount(),
        authRepository.currentUser,
        repository.allTransactions
    ) { revenue, count, lowStock, user, transactions ->
        val calendar = java.util.Calendar.getInstance()
        val now = calendar.timeInMillis
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -30)
        val monthStart = calendar.timeInMillis

        val monthlySales = transactions
            .filter { it.transaction.timestamp in monthStart..now }
            .sumOf { it.transaction.totalAmount }

        DashboardUiState(
            metrics = DashboardMetrics(
                dailySales = revenue ?: 0.0,
                monthlySales = monthlySales,
                ordersCount = count,
                lowStockCount = lowStock
            ),
            isAdmin = user?.role == "Admin",
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )
}
