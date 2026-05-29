package com.example.shreeganesh.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shreeganesh.data.repository.POSRepository
import com.example.shreeganesh.domain.models.ReportPeriod
import com.example.shreeganesh.domain.models.SalesReport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val repository: POSRepository
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(ReportPeriod.DAILY)

    val uiState: StateFlow<ReportsUiState> = combine(
        repository.getRevenueForToday(),
        repository.getTransactionCountForToday(),
        repository.allTransactions,
        _selectedPeriod
    ) { revenue, count, transactions, period ->
        
        // Calculate Chart Data (Last 7 Days)
        val chartData = calculateLast7DaysRevenue(transactions)

        ReportsUiState(
            report = SalesReport(
                dailyRevenue = revenue ?: 0.0,
                weeklyRevenue = 0.0,
                monthlyRevenue = 0.0,
                transactionCount = count
            ),
            transactions = transactions,
            chartData = chartData,
            selectedPeriod = period
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportsUiState()
    )

    private fun calculateLast7DaysRevenue(transactions: List<com.example.shreeganesh.data.local.dao.TransactionWithItems>): List<Pair<String, Double>> {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val result = mutableListOf<Pair<String, Double>>()

        for (i in 6 downTo 0) {
            val dateCalendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -i)
            }
            val dateStr = dateFormat.format(dateCalendar.time)
            
            val dayStart = dateCalendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val dayEnd = dayStart + (24 * 60 * 60 * 1000)

            val dayRevenue = transactions
                .filter { it.transaction.timestamp in dayStart until dayEnd }
                .sumOf { it.transaction.totalAmount }

            result.add(dateStr to dayRevenue)
        }
        return result
    }

    fun onPeriodSelected(period: ReportPeriod) {
        _selectedPeriod.value = period
    }
}
