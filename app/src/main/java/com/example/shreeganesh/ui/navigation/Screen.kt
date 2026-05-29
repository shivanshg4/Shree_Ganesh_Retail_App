package com.example.shreeganesh.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Dashboard : Screen("dashboard")
    object Billing : Screen("billing")
    object ProductManagement : Screen("product_management")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
    object Login : Screen("login")
    object UserManagement : Screen("user_management")
}
