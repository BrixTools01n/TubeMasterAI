package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Platform
import com.example.model.ToolOutputType

@Entity(tableName = "saved_items")
data class SavedItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val toolId: String,
    val toolName: String,
    val platform: String, // Platform enum name
    val outputType: String, // ToolOutputType enum name
    val title: String,
    val promptSummary: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
