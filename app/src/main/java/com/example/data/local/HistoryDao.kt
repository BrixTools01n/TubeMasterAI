package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM generation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM generation_history WHERE toolId = :toolId ORDER BY timestamp DESC LIMIT :limit")
    fun getHistoryForTool(toolId: String, limit: Int = 10): Flow<List<HistoryEntity>>

    @Query("SELECT COUNT(*) FROM generation_history")
    fun getHistoryCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryEntity): Long

    @Delete
    suspend fun delete(item: HistoryEntity)

    @Query("DELETE FROM generation_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM generation_history")
    suspend fun deleteAll()
}
