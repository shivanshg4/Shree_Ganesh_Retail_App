package com.example.shreeganesh.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shreeganesh.ui.components.POSInputField
import com.example.shreeganesh.ui.theme.spacing

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToUsers: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreenContent(
        uiState = uiState,
        onStoreNameChange = viewModel::updateStoreName,
        onStoreAddressChange = viewModel::updateStoreAddress,
        onTaxRateChange = { viewModel.updateTaxRate(it.toDoubleOrNull() ?: 0.0) },
        onDarkModeToggle = viewModel::toggleDarkMode,
        onNavigateToUsers = onNavigateToUsers
    )
}

@Composable
fun SettingsScreenContent(
    uiState: SettingsUiState,
    onStoreNameChange: (String) -> Unit,
    onStoreAddressChange: (String) -> Unit,
    onTaxRateChange: (String) -> Unit,
    onDarkModeToggle: (Boolean) -> Unit,
    onNavigateToUsers: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.gutter)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.large)
        )

        if (uiState.isAdmin) {
            // User Management
            SettingsSectionHeader(title = "Account Management", icon = Icons.Default.Group)
            ListItem(
                headlineContent = { Text("Staff Accounts") },
                supportingContent = { Text("Manage cashier accounts and permissions") },
                leadingContent = { Icon(Icons.Default.Group, contentDescription = null) },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
                modifier = Modifier
                    .clickable(onClick = onNavigateToUsers)
                    .padding(vertical = MaterialTheme.spacing.small)
            )
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
            Divider()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            // Store Information
            SettingsSectionHeader(title = "Store Information", icon = Icons.Default.Business)
            POSInputField(
                value = uiState.settings.storeName,
                onValueChange = onStoreNameChange,
                label = "Store Name",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            POSInputField(
                value = uiState.settings.storeAddress,
                onValueChange = onStoreAddressChange,
                label = "Store Address",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
            Divider()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            // Financials
            SettingsSectionHeader(title = "Financials", icon = Icons.Default.Payments)
            POSInputField(
                value = uiState.settings.taxRate.toString(),
                onValueChange = onTaxRateChange,
                label = "Tax Rate (%)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
            Divider()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
        }

        // Appearance
        SettingsSectionHeader(title = "Appearance", icon = Icons.Default.ColorLens)
        ListItem(
            headlineContent = { Text("Dark Mode") },
            supportingContent = { Text("Use a darker theme for the application") },
            trailingContent = {
                Switch(
                    checked = uiState.isDarkMode,
                    onCheckedChange = onDarkModeToggle
                )
            },
            modifier = Modifier.padding(vertical = MaterialTheme.spacing.small)
        )
    }
}

@Composable
fun SettingsSectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = MaterialTheme.spacing.small)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
