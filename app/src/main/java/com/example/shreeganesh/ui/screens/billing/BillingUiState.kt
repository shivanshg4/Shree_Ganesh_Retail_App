package com.example.shreeganesh.ui.screens.billing

import com.example.shreeganesh.domain.models.CartItem
import com.example.shreeganesh.domain.models.Category
import com.example.shreeganesh.domain.models.Product

enum class PaymentMethod { CASH, UPI, CARD }

data class BillingUiState(
    val availableProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null, // null means "All"
    val cartItems: List<CartItem> = emptyList(),
    val taxRatePercent: Double = 8.0,
    val isLoading: Boolean = false,
    val isCheckoutSuccess: Boolean = false,
    val error: String? = null
) {
    val subtotal: Double
        get() = cartItems.sumOf { it.totalPrice }

    val tax: Double
        get() = subtotal * (taxRatePercent / 100.0)

    val total: Double
        get() = subtotal + tax

    val totalItemCount: Int
        get() = cartItems.sumOf { it.quantity }
}
