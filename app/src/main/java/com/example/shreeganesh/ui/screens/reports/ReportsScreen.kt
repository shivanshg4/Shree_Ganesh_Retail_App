package com.example.shreeganesh.ui.screens.reports

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shreeganesh.data.local.dao.TransactionWithItems
import com.example.shreeganesh.domain.models.ReportPeriod
import com.example.shreeganesh.ui.theme.LocalSpacing
import com.example.shreeganesh.ui.theme.spacing
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTransaction by remember { mutableStateOf<TransactionWithItems?>(null) }
    val spacing = LocalSpacing.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Reports") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val isTablet = maxWidth > 800.dp
            
            Column(modifier = Modifier.fillMaxSize()) {
                // Period Selector
                TabRow(
                    selectedTabIndex = uiState.selectedPeriod.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    ReportPeriod.values().forEach { period ->
                        Tab(
                            selected = uiState.selectedPeriod == period,
                            onClick = { viewModel.onPeriodSelected(period) },
                            text = { Text(period.name) }
                        )
                    }
                }

                if (isTablet) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(spacing.medium),
                        horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                    ) {
                        // Left Column: Metrics and Chart
                        Column(modifier = Modifier.weight(1.2f).fillMaxHeight()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(spacing.medium)) {
                                val revenueTitle = when (uiState.selectedPeriod) {
                                    ReportPeriod.DAILY -> "Today's Revenue"
                                    ReportPeriod.WEEKLY -> "This Week's Revenue"
                                    ReportPeriod.MONTHLY -> "This Month's Revenue"
                                    ReportPeriod.YEARLY -> "This Year's Revenue"
                                }
                                val revenueValue = when (uiState.selectedPeriod) {
                                    ReportPeriod.DAILY -> uiState.report.dailyRevenue
                                    ReportPeriod.WEEKLY -> uiState.report.weeklyRevenue
                                    ReportPeriod.MONTHLY -> uiState.report.monthlyRevenue
                                    ReportPeriod.YEARLY -> uiState.report.yearlyRevenue
                                }
                                ReportMetricCard(
                                    title = revenueTitle,
                                    value = "₹%.2f".format(revenueValue),
                                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                                    modifier = Modifier.weight(1f)
                                )
                                ReportMetricCard(
                                    title = "Transactions",
                                    value = uiState.report.transactionCount.toString(),
                                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(spacing.medium))
                            
                            RevenueChart(
                                chartData = uiState.chartData,
                                modifier = Modifier.weight(1f).fillMaxWidth()
                            )
                        }

                        // Right Column: Transaction List
                        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = MaterialTheme.shapes.large,
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 1.dp
                            ) {
                                Column(modifier = Modifier.padding(spacing.medium)) {
                                    Text(
                                        text = "Recent Transactions",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = spacing.small)
                                    )
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(spacing.small)
                                    ) {
                                        items(uiState.transactions.take(50)) { transaction ->
                                            TransactionItem(
                                                transaction = transaction,
                                                onClick = { selectedTransaction = transaction }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Mobile Layout
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(spacing.medium)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                            ) {
                                val revenueTitle = when (uiState.selectedPeriod) {
                                    ReportPeriod.DAILY -> "Today's Revenue"
                                    ReportPeriod.WEEKLY -> "This Week's Revenue"
                                    ReportPeriod.MONTHLY -> "This Month's Revenue"
                                    ReportPeriod.YEARLY -> "This Year's Revenue"
                                }
                                val revenueValue = when (uiState.selectedPeriod) {
                                    ReportPeriod.DAILY -> uiState.report.dailyRevenue
                                    ReportPeriod.WEEKLY -> uiState.report.weeklyRevenue
                                    ReportPeriod.MONTHLY -> uiState.report.monthlyRevenue
                                    ReportPeriod.YEARLY -> uiState.report.yearlyRevenue
                                }
                                ReportMetricCard(
                                    title = revenueTitle,
                                    value = "₹%.2f".format(revenueValue),
                                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                                    modifier = Modifier.weight(1f)
                                )
                                ReportMetricCard(
                                    title = "Transactions",
                                    value = uiState.report.transactionCount.toString(),
                                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            RevenueChart(
                                chartData = uiState.chartData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            )
                        }

                        item {
                            Text(
                                text = "Recent Transactions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = spacing.small)
                            )
                        }

                        items(uiState.transactions.take(20)) { transaction ->
                            TransactionItem(
                                transaction = transaction,
                                onClick = { selectedTransaction = transaction }
                            )
                        }
                    }
                }
            }
        }
    }

    if (selectedTransaction != null) {
        TransactionDetailDialog(
            transaction = selectedTransaction!!,
            onDismiss = { selectedTransaction = null }
        )
    }
}

@Composable
fun RevenueChart(
    chartData: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (chartData.isEmpty()) return
    val maxValue = (chartData.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(1.0)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
            // Since we can't easily pass the period to the chart down here cleanly without changing signature,
            // we will let the chart title be generic.
            Text(
                "Revenue Report",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                verticalAlignment = Alignment.Bottom
            ) {
                chartData.forEach { (label, value) ->
                    val heightFraction = (value / maxValue).toFloat().coerceIn(0f, 1f)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "₹%.0f".format(value),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((140 * heightFraction).dp.coerceAtLeast(4.dp))
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.small
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(spacing.medium)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(spacing.small))
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionWithItems,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val dateFormat = remember { SimpleDateFormat("HH:mm • dd MMM", Locale.getDefault()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Receipt #${transaction.transaction.id}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = dateFormat.format(Date(transaction.transaction.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "₹%.2f".format(transaction.transaction.totalAmount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun TransactionDetailDialog(
    transaction: TransactionWithItems,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Transaction Details #${transaction.transaction.id}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                transaction.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.quantity}x ${item.productName}", modifier = Modifier.weight(1f))
                        Text("₹%.2f".format(item.productPrice * item.quantity))
                    }
                }
                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tax", fontWeight = FontWeight.SemiBold)
                    Text("₹%.2f".format(transaction.transaction.taxAmount))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "₹%.2f".format(transaction.transaction.totalAmount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}
