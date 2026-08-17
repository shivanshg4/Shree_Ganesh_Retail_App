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
        
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        // Weekly (Last 7 Days)
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val weeklyStart = calendar.timeInMillis
        val weeklyRevenue = transactions
            .filter { it.transaction.timestamp in weeklyStart..now }
            .sumOf { it.transaction.totalAmount }

        // Reset and calc Monthly (Last 30 Days)
        calendar.timeInMillis = now
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val monthlyStart = calendar.timeInMillis
        val monthlyRevenue = transactions
            .filter { it.transaction.timestamp in monthlyStart..now }
            .sumOf { it.transaction.totalAmount }

        // Reset and calc Yearly (Last 365 Days)
        calendar.timeInMillis = now
        calendar.add(Calendar.DAY_OF_YEAR, -365)
        val yearlyStart = calendar.timeInMillis
        val yearlyRevenue = transactions
            .filter { it.transaction.timestamp in yearlyStart..now }
            .sumOf { it.transaction.totalAmount }

        // Determine correct chart data based on period
        val chartData = when (period) {
            ReportPeriod.DAILY -> calculateLast7DaysRevenue(transactions)
            ReportPeriod.WEEKLY -> calculateLast4WeeksRevenue(transactions)
            ReportPeriod.MONTHLY -> calculateLast12MonthsRevenue(transactions)
            ReportPeriod.YEARLY -> calculateLast5YearsRevenue(transactions)
        }

        ReportsUiState(
            report = SalesReport(
                dailyRevenue = revenue ?: 0.0,
                weeklyRevenue = weeklyRevenue,
                monthlyRevenue = monthlyRevenue,
                yearlyRevenue = yearlyRevenue,
                transactionCount = transactions.size // Count all filtered? or let's use all for now.
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


    private fun calculateLast4WeeksRevenue(transactions: List<com.example.shreeganesh.data.local.dao.TransactionWithItems>): List<Pair<String, Double>> {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val result = mutableListOf<Pair<String, Double>>()

        for (i in 3 downTo 0) {
            val endCalendar = Calendar.getInstance().apply {
                add(Calendar.WEEK_OF_YEAR, -i)
            }
            val startCalendar = Calendar.getInstance().apply {
                add(Calendar.WEEK_OF_YEAR, -i)
                add(Calendar.DAY_OF_YEAR, -7)
            }
            val dateStr = dateFormat.format(startCalendar.time)

            val weekStart = startCalendar.timeInMillis
            val weekEnd = endCalendar.timeInMillis

            val weekRevenue = transactions
                .filter { it.transaction.timestamp in weekStart until weekEnd }
                .sumOf { it.transaction.totalAmount }

            result.add(dateStr to weekRevenue)
        }
        return result
    }

    private fun calculateLast12MonthsRevenue(transactions: List<com.example.shreeganesh.data.local.dao.TransactionWithItems>): List<Pair<String, Double>> {
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val result = mutableListOf<Pair<String, Double>>()

        for (i in 5 downTo 0) {
            val dateCalendar = Calendar.getInstance().apply {
                add(Calendar.MONTH, -i)
            }
            val dateStr = dateFormat.format(dateCalendar.time)

            val monthStart = dateCalendar.apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val monthEnd = Calendar.getInstance().apply {
                add(Calendar.MONTH, -i)
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val monthRevenue = transactions
                .filter { it.transaction.timestamp in monthStart..monthEnd }
                .sumOf { it.transaction.totalAmount }

            result.add(dateStr to monthRevenue)
        }
        return result
    }

    private fun calculateLast5YearsRevenue(transactions: List<com.example.shreeganesh.data.local.dao.TransactionWithItems>): List<Pair<String, Double>> {
        val dateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val result = mutableListOf<Pair<String, Double>>()

        for (i in 4 downTo 0) {
            val dateCalendar = Calendar.getInstance().apply {
                add(Calendar.YEAR, -i)
            }
            val dateStr = dateFormat.format(dateCalendar.time)

            val yearStart = dateCalendar.apply {
                set(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val yearEnd = Calendar.getInstance().apply {
                add(Calendar.YEAR, -i)
                set(Calendar.MONTH, Calendar.DECEMBER)
                set(Calendar.DAY_OF_MONTH, 31)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val yearRevenue = transactions
                .filter { it.transaction.timestamp in yearStart..yearEnd }
                .sumOf { it.transaction.totalAmount }

            result.add(dateStr to yearRevenue)
        }
        return result
    }

    fun onPeriodSelected(period: ReportPeriod) {
        _selectedPeriod.value = period
    }
}
