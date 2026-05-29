package com.example.shreeganesh.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val categoryId: String? = null,
    val imageUrl: String? = null,
    val stockQuantity: Int = 0
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val color: Int
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val totalAmount: Double,
    val taxAmount: Double,
    val paymentMethod: String = "Cash",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "transaction_items")
data class TransactionItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: Long,
    val productId: String,
    val productName: String,
    val productPrice: Double,
    val quantity: Int
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 0, // Single row for app settings
    val storeName: String,
    val storeAddress: String,
    val taxRate: Double,
    val currencySymbol: String
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val role: String,
    val pin: String // In production, this should be hashed
)
