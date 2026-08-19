package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE email LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchUsers(query: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET plan = :plan, subscriptionStatus = :status WHERE id = :userId")
    suspend fun updatePlan(userId: String, plan: String, status: String)

    @Query("UPDATE users SET isSuspended = :isSuspended WHERE id = :userId")
    suspend fun setSuspended(userId: String, isSuspended: Boolean)

    @Query("UPDATE users SET generationCount = :count, limitReachedAt = :limitReachedAt WHERE id = :userId")
    suspend fun updateUsage(userId: String, count: Int, limitReachedAt: Long?)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT COUNT(*) FROM users WHERE plan = 'pro' OR subscriptionStatus = 'PRO'")
    suspend fun getProUserCount(): Int
}
