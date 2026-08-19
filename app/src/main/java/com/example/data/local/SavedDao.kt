package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedDao {
    @Query("SELECT * FROM saved_items ORDER BY timestamp DESC")
    fun getAllSaved(): Flow<List<SavedItemEntity>>

    @Query("SELECT * FROM saved_items WHERE platform = :platform ORDER BY timestamp DESC")
    fun getSavedByPlatform(platform: String): Flow<List<SavedItemEntity>>

    @Query("SELECT * FROM saved_items WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<SavedItemEntity>>

    @Query("SELECT COUNT(*) FROM saved_items")
    fun getSavedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM saved_items WHERE isFavorite = 1")
    fun getFavoritesCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SavedItemEntity): Long

    @Update
    suspend fun update(item: SavedItemEntity)

    @Delete
    suspend fun delete(item: SavedItemEntity)

    @Query("DELETE FROM saved_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM saved_items")
    suspend fun deleteAll()
}
