package com.example.shreeganesh.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shreeganesh.domain.models.DashboardMetrics
import com.example.shreeganesh.ui.components.POSMetricCard
import com.example.shreeganesh.ui.components.POSPrimaryButton
import com.example.shreeganesh.ui.components.POSTonalButton
import com.example.shreeganesh.ui.theme.ShreeGaneshTheme
import com.example.shreeganesh.ui.theme.spacing

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onNavigateToBilling: () -> Unit = {},
    onNavigateToProducts: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    DashboardScreenContent(
        uiState = uiState,
        onNavigateToBilling = onNavigateToBilling,
        onNavigateToProducts = onNavigateToProducts,
        onNavigateToReports = onNavigateToReports,
        onNavigateToSettings = onNavigateToSettings
    )
}

@Composable
fun DashboardScreenContent(
    uiState: DashboardUiState,
    onNavigateToBilling: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val isTablet = maxWidth > 600.dp
        
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.gutter)) {
                
                Text(
                    text = "Store Dashboard",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
                )

                // Metrics Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (isTablet) 2 else 1),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        POSMetricCard(
                            title = "Daily Sales",
                            value = "₹%.2f".format(uiState.metrics.dailySales),
                            icon = Icons.Default.PointOfSale,
                            iconTint = MaterialTheme.colorScheme.primary
                        )
                    }
                    item {
                        POSMetricCard(
                            title = "Monthly Sales",
                            value = "₹%.2f".format(uiState.metrics.monthlySales),
                            icon = Icons.Default.AttachMoney,
                            iconTint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    item {
                        POSMetricCard(
                            title = "Orders Today",
                            value = uiState.metrics.ordersCount.toString(),
                            icon = Icons.Default.ShoppingCart,
                            iconTint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    item {
                        POSMetricCard(
                            title = "Low Stock Alerts",
                            value = uiState.metrics.lowStockCount.toString(),
                            icon = Icons.Default.Warning,
                            iconTint = Color(0xFFD32F2F) // Material Red 700
                        )
                    }
                }

                // Quick Actions
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    modifier = Modifier.padding(top = MaterialTheme.spacing.gutter)
                ) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    if (isTablet) {
                        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
                            POSPrimaryButton(text = "Start Billing", onClick = onNavigateToBilling, modifier = Modifier.weight(1f))
                            if (uiState.isAdmin) {
                                POSTonalButton(text = "Manage Products", onClick = onNavigateToProducts, modifier = Modifier.weight(1f))
                                POSTonalButton(text = "View Reports", onClick = onNavigateToReports, modifier = Modifier.weight(1f))
                            }
                            POSTonalButton(text = "Settings", onClick = onNavigateToSettings, modifier = Modifier.weight(1f))
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
                            POSPrimaryButton(text = "Start Billing", onClick = onNavigateToBilling, modifier = Modifier.fillMaxWidth())
                            if (uiState.isAdmin) {
                                POSTonalButton(text = "Manage Products", onClick = onNavigateToProducts, modifier = Modifier.fillMaxWidth())
                                POSTonalButton(text = "View Reports", onClick = onNavigateToReports, modifier = Modifier.fillMaxWidth())
                            }
                            POSTonalButton(text = "Settings", onClick = onNavigateToSettings, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}

@Preview(device = "id:pixel_tablet", showSystemUi = true)
@Composable
fun DashboardScreenTabletPreview() {
    ShreeGaneshTheme {
        DashboardScreenContent(
            uiState = DashboardUiState(
                metrics = DashboardMetrics(1245.50, 34500.0, 84, 5),
                isLoading = false
            ),
            onNavigateToBilling = {},
            onNavigateToProducts = {},
            onNavigateToReports = {},
            onNavigateToSettings = {}
        )
    }
}

@Preview(device = "id:pixel_7", showSystemUi = true)
@Composable
fun DashboardScreenMobilePreview() {
    ShreeGaneshTheme {
        DashboardScreenContent(
            uiState = DashboardUiState(
                metrics = DashboardMetrics(1245.50, 34500.0, 84, 5),
                isLoading = false
            ),
            onNavigateToBilling = {},
            onNavigateToProducts = {},
            onNavigateToReports = {},
            onNavigateToSettings = {}
        )
    }
}
