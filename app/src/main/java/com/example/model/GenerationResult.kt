package com.example.model

data class GenerationResult(
    val toolId: String,
    val toolName: String,
    val platform: Platform,
    val outputType: ToolOutputType,
    val rawText: String,
    val items: List<String> = emptyList(),
    val sections: Map<String, String> = emptyMap(),
    val tags: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)
