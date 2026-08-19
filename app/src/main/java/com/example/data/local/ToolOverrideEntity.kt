package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tool_overrides")
data class ToolOverrideEntity(
    @PrimaryKey
    val toolId: String,
    val isProOverride: Boolean? = null,
    val isDisabled: Boolean = false,
    val isDeleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface ToolOverrideDao {
    @Query("SELECT * FROM tool_overrides")
    fun getAllOverrides(): Flow<List<ToolOverrideEntity>>

    @Query("SELECT * FROM tool_overrides WHERE toolId = :toolId LIMIT 1")
    suspend fun getOverride(toolId: String): ToolOverrideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOverride(override: ToolOverrideEntity)

    @Query("DELETE FROM tool_overrides WHERE toolId = :toolId")
    suspend fun deleteOverride(toolId: String)
}
