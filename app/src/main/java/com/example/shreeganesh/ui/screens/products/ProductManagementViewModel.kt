package com.example.shreeganesh.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shreeganesh.data.repository.POSRepository
import com.example.shreeganesh.domain.models.Category
import com.example.shreeganesh.domain.models.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductManagementViewModel @Inject constructor(
    private val repository: POSRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ProductManagementUiState> = combine(
        repository.allProducts,
        repository.allCategories,
        _searchQuery
    ) { products, categories, query ->
        ProductManagementUiState(
            allProducts = if (query.isEmpty()) products else products.filter { it.name.contains(query, ignoreCase = true) },
            categories = categories,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductManagementUiState()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addProduct(name: String, price: Double, categoryId: String?, stockQuantity: Int) {
        viewModelScope.launch {
            val product = Product(
                id = System.currentTimeMillis().toString(),
                name = name,
                price = price,
                categoryId = categoryId,
                stockQuantity = stockQuantity
            )
            repository.addProduct(product)
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            val category = Category(
                id = System.currentTimeMillis().toString(),
                name = name,
                color = 0 // Default color
            )
            repository.addCategory(category)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }
}
