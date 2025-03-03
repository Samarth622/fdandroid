package com.example.foodlens

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User) {
        userDao.registerUser(user)
    }

    suspend fun loginUser(mobile: String, password: String): Boolean {
        val user = userDao.loginUser(mobile, password)
        return user != null
    }

    suspend fun isMobileRegistered(mobile: String): Boolean {
        return userDao.isMobileRegistered(mobile) > 0
    }
}
