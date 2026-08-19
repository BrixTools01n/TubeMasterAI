package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val provider: String = "email", // "email" or "google"
    val passwordHash: String = "",
    val role: String = "user", // "user" or "admin"
    val plan: String = "free", // "free" or "pro"
    val subscriptionStatus: String = "FREE", // "FREE", "PAYMENT_INITIATED", "PENDING_VERIFICATION", "PRO"
    val generationCount: Int = 0,
    val limitReachedAt: Long? = null, // timestamp when 12th generation was consumed
    val isSuspended: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)
