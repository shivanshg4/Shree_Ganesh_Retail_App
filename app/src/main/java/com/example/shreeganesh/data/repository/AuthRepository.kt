package com.example.shreeganesh.data.repository

import com.example.shreeganesh.data.local.dao.UserDao
import com.example.shreeganesh.data.local.entities.UserEntity
import com.example.shreeganesh.domain.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao
) {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val allUsers: Flow<List<User>> = userDao.getAllUsers().map { entities ->
        entities.map { User(it.id, it.name, it.role) }
    }

    suspend fun login(pin: String): Boolean {
        // Ensure default user exists on first login attempt if none found
        if (userDao.getUserCount() == 0) {
            userDao.insertUser(UserEntity("1", "Admin", "Admin", "1234"))
        }

        val user = userDao.getUserByPin(pin)
        return if (user != null) {
            _currentUser.value = user
            true
        } else {
            false
        }
    }

    suspend fun addUser(name: String, role: String, pin: String) {
        val id = java.util.UUID.randomUUID().toString()
        userDao.insertUser(UserEntity(id, name, role, pin))
    }

    suspend fun deleteUser(userId: String) {
        val user = userDao.getUserById(userId)
        if (user != null && user.role != "Admin") { // Prevent deleting admins for now
            userDao.deleteUser(user)
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun isAdmin(): Boolean = _currentUser.value?.role == "Admin"
}
