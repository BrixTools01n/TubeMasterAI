package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val planId: String,
    val planName: String,
    val amount: Int,
    val currency: String = "INR",
    val method: String,
    val reference: String,
    val status: String, // "pending", "verified", "failed"
    val createdAt: Long = System.currentTimeMillis(),
    val verifiedAt: Long? = null
)

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY createdAt DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPaymentsByUser(userId: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE id = :id LIMIT 1")
    suspend fun getPaymentById(id: String): PaymentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Query("UPDATE payments SET status = :status, verifiedAt = :verifiedAt WHERE id = :id")
    suspend fun updatePaymentStatus(id: String, status: String, verifiedAt: Long?)

    @Query("SELECT SUM(amount) FROM payments WHERE status = 'verified'")
    suspend fun getTotalVerifiedRevenue(): Long?

    @Query("SELECT SUM(amount) FROM payments WHERE status = 'verified' AND createdAt >= :sinceTimestamp")
    suspend fun getVerifiedRevenueSince(sinceTimestamp: Long): Long?

    @Query("SELECT COUNT(*) FROM payments WHERE status = 'verified'")
    suspend fun getVerifiedPaymentCount(): Int

    @Query("DELETE FROM payments WHERE userId = :userId")
    suspend fun deletePaymentsByUser(userId: String)
}
