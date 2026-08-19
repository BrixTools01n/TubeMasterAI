package com.example.model

data class ToolConfig(
    val id: String,
    val name: String,
    val platform: Platform,
    val category: String,
    val description: String,
    val iconKey: String,
    val isPro: Boolean = false,
    val outputType: ToolOutputType = ToolOutputType.TEXT,
    val fields: List<InputField> = emptyList(),
    val promptTemplate: String = "",
    val keywords: List<String> = emptyList()
)
