package com.example.ai

import com.example.model.GenerationResult
import com.example.model.ToolConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AIEngine(
    private val openRouterProvider: OpenRouterProvider = OpenRouterProvider(),
    private val geminiProvider: GeminiAIProvider = GeminiAIProvider(),
    private val mockProvider: MockAIProvider = MockAIProvider()
) : AIProvider {

    private val _isUsingMock = MutableStateFlow(true)
    val isUsingMock: StateFlow<Boolean> = _isUsingMock.asStateFlow()

    private val _activeProviderName = MutableStateFlow("Mock (Offline)")
    val activeProviderName: StateFlow<String> = _activeProviderName.asStateFlow()

    override suspend fun generate(
        tool: ToolConfig,
        inputs: Map<String, Any>,
        language: String
    ): Result<GenerationResult> {
        // Try OpenRouter first if configured
        val openRouterResult = openRouterProvider.generate(tool, inputs, language)
        if (openRouterResult.isSuccess) {
            _isUsingMock.value = false
            _activeProviderName.value = "OpenRouter AI"
            return openRouterResult
        }

        // Try Gemini next if configured
        val geminiResult = geminiProvider.generate(tool, inputs, language)
        if (geminiResult.isSuccess) {
            _isUsingMock.value = false
            _activeProviderName.value = "Google Gemini AI"
            return geminiResult
        }

        // Fallback safely to contextual Mock AI Engine
        _isUsingMock.value = true
        _activeProviderName.value = "Contextual AI Engine (Local)"
        return mockProvider.generate(tool, inputs, language)
    }
}
