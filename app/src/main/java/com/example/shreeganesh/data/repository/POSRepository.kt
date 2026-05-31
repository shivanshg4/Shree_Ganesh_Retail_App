package com.example.shreeganesh.data.repository

import com.example.shreeganesh.data.local.dao.CategoryDao
import com.example.shreeganesh.data.local.dao.ProductDao
import com.example.shreeganesh.data.local.dao.SettingsDao
import com.example.shreeganesh.data.local.dao.TransactionDao
import com.example.shreeganesh.data.local.dao.TransactionWithItems
import com.example.shreeganesh.data.local.entities.CategoryEntity
import com.example.shreeganesh.data.local.entities.ProductEntity
import com.example.shreeganesh.data.local.entities.TransactionEntity
import com.example.shreeganesh.data.local.entities.TransactionItemEntity
import com.example.shreeganesh.domain.models.CartItem
import com.example.shreeganesh.domain.models.Category
import com.example.shreeganesh.domain.models.Product
import com.example.shreeganesh.domain.models.StoreSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class POSRepository(
    private val productDao: ProductDao,
    private val transactionDao: TransactionDao,
    private val settingsDao: SettingsDao,
    private val categoryDao: CategoryDao
) {
    val allProducts: Flow<List<Product>> = productDao.getAllProducts().map { entities ->
        entities.map { Product(it.id, it.name, it.price, imageUrl = it.imageUrl, categoryId = it.categoryId, stockQuantity = it.stockQuantity) }
    }

    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories().map { entities ->
        entities.map { Category(it.id, it.name, it.color) }
    }

    suspend fun addProduct(product: Product) {
        productDao.insertProduct(
            ProductEntity(
                id = product.id, 
                name = product.name, 
                price = product.price, 
                categoryId = product.categoryId,
                imageUrl = product.imageUrl,
                stockQuantity = product.stockQuantity
            )
        )
    }
    
    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(
            ProductEntity(
                id = product.id,
                name = product.name,
                price = product.price,
                categoryId = product.categoryId,
                imageUrl = product.imageUrl,
                stockQuantity = product.stockQuantity
            )
        )
    }

    suspend fun addCategory(category: Category) {
        categoryDao.insertCategory(CategoryEntity(category.id, category.name, category.color))
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(CategoryEntity(category.id, category.name, category.color))
    }

    suspend fun deleteProduct(productId: String) {
        val product = productDao.getProductById(productId)
        if (product != null) {
            productDao.deleteProduct(product)
        }
    }

    val storeSettings: Flow<StoreSettings?> = settingsDao.getSettings().map { entity ->
        entity?.let { StoreSettings(it.storeName, it.storeAddress, it.taxRate, it.currencySymbol) }
    }

    suspend fun updateSettings(settings: StoreSettings) {
        settingsDao.updateSettings(
            com.example.shreeganesh.data.local.entities.SettingsEntity(
                storeName = settings.storeName,
                storeAddress = settings.storeAddress,
                taxRate = settings.taxRate,
                currencySymbol = settings.currencySymbol
            )
        )
    }

    // Transactions & Inventory
    val allTransactions: Flow<List<TransactionWithItems>> = transactionDao.getAllTransactionsWithItems()

    suspend fun completeTransaction(total: Double, tax: Double, cartItems: List<CartItem>) {
        // Record the transaction
        val transactionId = transactionDao.insertTransaction(
            TransactionEntity(totalAmount = total, taxAmount = tax)
        )
        
        // Record items and reduce stock
        val itemEntities = cartItems.map { item ->
            // Reduce stock in DB
            productDao.reduceStock(item.product.id, item.quantity)
            
            TransactionItemEntity(
                transactionId = transactionId,
                productId = item.product.id,
                productName = item.product.name,
                productPrice = item.product.price,
                quantity = item.quantity
            )
        }
        transactionDao.insertTransactionItems(itemEntities)
    }

    // Reporting & Dashboard Helpers
    fun getRevenueForToday(): Flow<Double?> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        return transactionDao.getRevenueSince(calendar.timeInMillis)
    }

    fun getTransactionCountForToday(): Flow<Int> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        return transactionDao.getTransactionCountSince(calendar.timeInMillis)
    }

    fun getRevenueBetween(startTime: Long, endTime: Long): Flow<Double?> {
        return transactionDao.getRevenueBetween(startTime, endTime)
    }

    fun getLowStockCount(threshold: Int = 5): Flow<Int> {
        return productDao.getLowStockCount(threshold)
    }

    suspend fun seedData() {
        if (categoryDao.getCategoryCount() == 0) {
            val catFoodId = "cat_food"
            val catDrinkId = "cat_drink"
            
            categoryDao.insertCategory(CategoryEntity(catFoodId, "Food", 0xFF2E7D32.toInt()))
            categoryDao.insertCategory(CategoryEntity(catDrinkId, "Drinks", 0xFF1B5E20.toInt()))
            
            productDao.insertProduct(ProductEntity("p1", "Classic Burger",  89.00,  catFoodId,  "burger",   20))
            productDao.insertProduct(ProductEntity("p2", "Pepperoni Pizza", 125.00, catFoodId,  "pizza",    15))
            productDao.insertProduct(ProductEntity("p3", "Cappuccino",       45.00, catDrinkId, "coffee",   50))
            productDao.insertProduct(ProductEntity("p4", "Club Sandwich",    75.00, catFoodId,  "sandwich", 12))
            productDao.insertProduct(ProductEntity("p5", "Greek Salad",      65.00, catFoodId,  "salad",    10))
        }
    }
}
