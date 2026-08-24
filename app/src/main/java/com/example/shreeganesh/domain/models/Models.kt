package com.example.shreeganesh.domain.models

import androidx.compose.runtime.Immutable

@Immutable
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String? = null,
    val categoryId: String? = null,
    val stockQuantity: Int = 0
)

@Immutable
data class Category(
    val id: String,
    val name: String,
    val color: Int
)

@Immutable
data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val totalPrice: Double
        get() = product.price * quantity
}

@Immutable
data class DashboardMetrics(
    val dailySales: Double,
    val monthlySales: Double,
    val ordersCount: Int,
    val lowStockCount: Int
)

@Immutable
data class SalesReport(
    val dailyRevenue: Double,
    val weeklyRevenue: Double,
    val monthlyRevenue: Double,
    val yearlyRevenue: Double,
    val transactionCount: Int
)

@Immutable
enum class ReportPeriod {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

@Immutable
data class User(
    val id: String,
    val name: String,
    val role: String
)

@Immutable
data class StoreSettings(
    val storeName: String,
    val storeAddress: String,
    val taxRate: Double,
    val currencySymbol: String
)
