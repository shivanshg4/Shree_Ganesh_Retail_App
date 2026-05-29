package com.example.shreeganesh.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shreeganesh.data.local.dao.CategoryDao
import com.example.shreeganesh.data.local.dao.ProductDao
import com.example.shreeganesh.data.local.dao.SettingsDao
import com.example.shreeganesh.data.local.dao.TransactionDao
import com.example.shreeganesh.data.local.dao.UserDao
import com.example.shreeganesh.data.local.entities.CategoryEntity
import com.example.shreeganesh.data.local.entities.ProductEntity
import com.example.shreeganesh.data.local.entities.SettingsEntity
import com.example.shreeganesh.data.local.entities.TransactionEntity
import com.example.shreeganesh.data.local.entities.TransactionItemEntity
import com.example.shreeganesh.data.local.entities.UserEntity

@Database(
    entities = [ProductEntity::class, TransactionEntity::class, TransactionItemEntity::class, SettingsEntity::class, UserEntity::class, CategoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao
    abstract fun settingsDao(): SettingsDao
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
 
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
 
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pos_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate default admin user
                        // Note: In a real app, use a CoroutineWorker or similar
                        // For simplicity, we'll assume the repository handles first-run setup
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
