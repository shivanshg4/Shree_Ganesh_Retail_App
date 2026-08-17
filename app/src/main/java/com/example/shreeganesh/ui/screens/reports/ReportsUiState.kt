package com.example.shreeganesh.ui.screens.reports

import com.example.shreeganesh.data.local.dao.TransactionWithItems
import com.example.shreeganesh.domain.models.ReportPeriod
import com.example.shreeganesh.domain.models.SalesReport

data class ReportsUiState(
    val report: SalesReport = SalesReport(0.0, 0.0, 0.0, 0.0, 0),
    val transactions: List<TransactionWithItems> = emptyList(),
    val chartData: List<Pair<String, Double>> = emptyList(),
    val selectedPeriod: ReportPeriod = ReportPeriod.DAILY,
    val isLoading: Boolean = false
)
