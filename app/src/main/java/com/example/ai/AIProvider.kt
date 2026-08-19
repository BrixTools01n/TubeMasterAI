package com.example.ai

import com.example.model.GenerationResult
import com.example.model.ToolConfig

interface AIProvider {
    suspend fun generate(
        tool: ToolConfig,
        inputs: Map<String, Any>,
        language: String = "English"
    ): Result<GenerationResult>
}
