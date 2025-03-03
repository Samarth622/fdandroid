package com.example.foodlens

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert
    suspend fun registerUser(user: User)

    @Query("SELECT * FROM users WHERE mobile = :mobile AND password = :password LIMIT 1")
    suspend fun loginUser(mobile: String, password: String): User?

    @Query("SELECT COUNT(*) FROM users WHERE mobile = :mobile")
    suspend fun isMobileRegistered(mobile: String): Int

    @Query("SELECT * FROM users WHERE id = :mobile")
     fun getUser(mobile: String): Flow<User>

}
