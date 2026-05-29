package com.example.shreeganesh.ui.screens.products

import com.example.shreeganesh.domain.models.Product
import com.example.shreeganesh.domain.models.Category

data class ProductManagementUiState(
    val allProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
) {
    val filteredProducts: List<Product>
        get() = if (searchQuery.isBlank()) {
            allProducts
        } else {
            allProducts.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
}
