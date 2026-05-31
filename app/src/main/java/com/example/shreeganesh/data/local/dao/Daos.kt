package com.example.shreeganesh.data.local.dao

import androidx.room.*
import com.example.shreeganesh.data.local.entities.CategoryEntity
import com.example.shreeganesh.data.local.entities.ProductEntity
import com.example.shreeganesh.data.local.entities.TransactionEntity
import com.example.shreeganesh.data.local.entities.TransactionItemEntity
import com.example.shreeganesh.data.local.entities.SettingsEntity
import com.example.shreeganesh.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun getProductsByCategory(categoryId: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET stockQuantity = stockQuantity - :quantity WHERE id = :productId")
    suspend fun reduceStock(productId: String, quantity: Int)

    @Query("SELECT COUNT(*) FROM products WHERE stockQuantity <= :threshold")
    fun getLowStockCount(threshold: Int = 5): Flow<Int>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): ProductEntity?
}

@Dao
interface TransactionDao {
    @Transaction
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsWithItems(): Flow<List<TransactionWithItems>>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert
    suspend fun insertTransactionItems(items: List<TransactionItemEntity>)

    @Query("SELECT SUM(totalAmount) FROM transactions WHERE timestamp >= :startTime")
    fun getRevenueSince(startTime: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM transactions WHERE timestamp >= :startTime")
    fun getTransactionCountSince(startTime: Long): Flow<Int>

    @Query("SELECT SUM(totalAmount) FROM transactions WHERE timestamp >= :startTime AND timestamp < :endTime")
    fun getRevenueBetween(startTime: Long, endTime: Long): Flow<Double?>
}

data class TransactionWithItems(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "transactionId"
    )
    val items: List<TransactionItemEntity>
)

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 0")
    fun getSettings(): Flow<SettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: SettingsEntity)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE pin = :pin")
    suspend fun getUserByPin(pin: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
}
