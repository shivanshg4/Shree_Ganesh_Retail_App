package com.example.shreeganesh.data.repository

import com.example.shreeganesh.data.local.dao.UserDao
import com.example.shreeganesh.data.local.entities.UserEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var userDao: UserDao
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        userDao = mockk(relaxed = true)
        authRepository = AuthRepository(userDao)
    }

    @Test
    fun `login with no existing users creates default admin and logs in if pin matches`() = runTest {
        // Arrange
        val pin = "1234"
        coEvery { userDao.getUserCount() } returns 0
        coEvery { userDao.getUserByPin(pin) } returns UserEntity("1", "Admin", "Admin", "1234")

        // Act
        val result = authRepository.login(pin)

        // Assert
        assertTrue(result)
        assertEquals("Admin", authRepository.currentUser.value?.role)
        coVerify { userDao.insertUser(any()) }
    }

    @Test
    fun `login with valid pin successfully logs in`() = runTest {
        // Arrange
        val pin = "5678"
        val mockUser = UserEntity("2", "Cashier", "Cashier", pin)
        coEvery { userDao.getUserCount() } returns 1
        coEvery { userDao.getUserByPin(pin) } returns mockUser

        // Act
        val result = authRepository.login(pin)

        // Assert
        assertTrue(result)
        assertEquals(mockUser, authRepository.currentUser.value)
        coVerify(exactly = 0) { userDao.insertUser(any()) }
    }

    @Test
    fun `login with invalid pin returns false`() = runTest {
        // Arrange
        val pin = "9999"
        coEvery { userDao.getUserCount() } returns 1
        coEvery { userDao.getUserByPin(pin) } returns null

        // Act
        val result = authRepository.login(pin)

        // Assert
        assertFalse(result)
        assertEquals(null, authRepository.currentUser.value)
    }
}
