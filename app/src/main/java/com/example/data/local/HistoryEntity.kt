package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generation_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val toolId: String,
    val toolName: String,
    val platform: String,
    val outputType: String,
    val inputSummary: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
