package com.example.shreeganesh.ui.screens.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shreeganesh.data.repository.POSRepository
import com.example.shreeganesh.domain.models.CartItem
import com.example.shreeganesh.domain.models.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val repository: POSRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    private val _isCheckoutSuccess = MutableStateFlow(false)

    val uiState: StateFlow<BillingUiState> = combine(
        repository.allProducts,
        repository.allCategories,
        _cartItems,
        _selectedCategoryId
    ) { products, categories, cartItems, selectedCatId ->
        val filteredProducts = if (selectedCatId == null) {
            products
        } else {
            products.filter { it.categoryId == selectedCatId }
        }

        BillingUiState(
            availableProducts = filteredProducts,
            categories = categories,
            selectedCategoryId = selectedCatId,
            cartItems = cartItems,
            isCheckoutSuccess = _isCheckoutSuccess.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BillingUiState()
    )

    fun onCategorySelected(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

    fun addToCart(product: Product) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems.find { it.product.id == product.id }
            if (existingItem != null) {
                currentItems.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                currentItems + CartItem(product, 1)
            }
        }
    }

    /**
     * quantityChange: +1 to increment, -1 to decrement (auto-removes if qty reaches 0)
     */
    fun updateQuantity(product: Product, quantityChange: Int) {
        _cartItems.update { currentItems ->
            currentItems.mapNotNull {
                if (it.product.id == product.id) {
                    val newQuantity = it.quantity + quantityChange
                    if (newQuantity > 0) it.copy(quantity = newQuantity) else null
                } else {
                    it
                }
            }
        }
    }

    fun removeItem(product: Product) {
        _cartItems.update { currentItems ->
            currentItems.filterNot { it.product.id == product.id }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun onPayClicked() {
        viewModelScope.launch {
            val state = uiState.value
            if (state.cartItems.isNotEmpty()) {
                repository.completeTransaction(state.total, state.tax, state.cartItems)
                clearCart()
                _isCheckoutSuccess.value = true
            }
        }
    }

    fun onCheckoutSuccessDismissed() {
        _isCheckoutSuccess.value = false
    }
}
