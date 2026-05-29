package com.example.shreeganesh.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.shreeganesh.ui.screens.billing.BillingScreen
import com.example.shreeganesh.ui.screens.billing.BillingViewModel
import com.example.shreeganesh.ui.screens.dashboard.DashboardScreen
import com.example.shreeganesh.ui.screens.dashboard.DashboardViewModel
import com.example.shreeganesh.ui.screens.login.LoginScreen
import com.example.shreeganesh.ui.screens.login.LoginViewModel
import com.example.shreeganesh.ui.screens.products.ProductManagementScreen
import com.example.shreeganesh.ui.screens.products.ProductManagementViewModel
import com.example.shreeganesh.ui.screens.reports.ReportsScreen
import com.example.shreeganesh.ui.screens.reports.ReportsViewModel
import com.example.shreeganesh.ui.screens.settings.SettingsScreen
import com.example.shreeganesh.ui.screens.settings.SettingsViewModel
import com.example.shreeganesh.ui.screens.splash.SplashScreen
import com.example.shreeganesh.ui.screens.users.UserManagementScreen
import com.example.shreeganesh.ui.screens.users.UserManagementViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        splashGraph(navController)
        loginGraph(navController)
        dashboardGraph(navController)
        billingGraph(navController)
        productManagementGraph(navController)
        reportsGraph(navController)
        settingsGraph(navController)
        userManagementGraph(navController)
    }
}

fun NavGraphBuilder.splashGraph(navController: NavController) {
    composable(Screen.Splash.route) {
        SplashScreen(
            onNavigateToDashboard = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        )
    }
}

fun NavGraphBuilder.loginGraph(navController: NavController) {
    composable(Screen.Login.route) {
        val loginViewModel: LoginViewModel = hiltViewModel()
        LoginScreen(
            viewModel = loginViewModel,
            onLoginSuccess = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        )
    }
}

fun NavGraphBuilder.dashboardGraph(navController: NavController) {
    composable(Screen.Dashboard.route) {
        val dashboardViewModel: DashboardViewModel = hiltViewModel()
        DashboardScreen(
            viewModel = dashboardViewModel,
            onNavigateToBilling = { navController.navigate(Screen.Billing.route) },
            onNavigateToProducts = { navController.navigate(Screen.ProductManagement.route) },
            onNavigateToReports = { navController.navigate(Screen.Reports.route) },
            onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
        )
    }
}

fun NavGraphBuilder.billingGraph(navController: NavController) {
    composable(Screen.Billing.route) {
        val billingViewModel: BillingViewModel = hiltViewModel()
        BillingScreen(
            viewModel = billingViewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

fun NavGraphBuilder.productManagementGraph(navController: NavController) {
    composable(Screen.ProductManagement.route) {
        val productViewModel: ProductManagementViewModel = hiltViewModel()
        ProductManagementScreen(
            viewModel = productViewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

fun NavGraphBuilder.reportsGraph(navController: NavController) {
    composable(Screen.Reports.route) {
        val reportsViewModel: ReportsViewModel = hiltViewModel()
        ReportsScreen(
            viewModel = reportsViewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

fun NavGraphBuilder.settingsGraph(navController: NavController) {
    composable(Screen.Settings.route) {
        val settingsViewModel: SettingsViewModel = hiltViewModel()
        SettingsScreen(
            viewModel = settingsViewModel,
            onNavigateToUsers = { navController.navigate(Screen.UserManagement.route) },
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

fun NavGraphBuilder.userManagementGraph(navController: NavController) {
    composable(Screen.UserManagement.route) {
        val userViewModel: UserManagementViewModel = hiltViewModel()
        UserManagementScreen(
            viewModel = userViewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
