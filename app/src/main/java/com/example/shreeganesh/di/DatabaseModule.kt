package com.example.shreeganesh.di

import android.content.Context
import com.example.shreeganesh.data.local.AppDatabase
import com.example.shreeganesh.data.local.dao.CategoryDao
import com.example.shreeganesh.data.local.dao.ProductDao
import com.example.shreeganesh.data.local.dao.SettingsDao
import com.example.shreeganesh.data.local.dao.TransactionDao
import com.example.shreeganesh.data.local.dao.UserDao
import com.example.shreeganesh.data.repository.POSRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Provides

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    fun provideSettingsDao(database: AppDatabase): SettingsDao {
        return database.settingsDao()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideRepository(
        productDao: ProductDao,
        transactionDao: TransactionDao,
        settingsDao: SettingsDao,
        categoryDao: CategoryDao
    ): POSRepository {
        return POSRepository(productDao, transactionDao, settingsDao, categoryDao)
    }
}
