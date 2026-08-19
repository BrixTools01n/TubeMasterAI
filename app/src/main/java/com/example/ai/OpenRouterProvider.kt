package com.example.ai

import com.example.BuildConfig
import com.example.model.GenerationResult
import com.example.model.ToolConfig
import com.example.model.ToolOutputType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class OpenRouterRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val temperature: Float = 0.7f,
    @Json(name = "max_tokens") val maxTokens: Int = 1500
)

@JsonClass(generateAdapter = true)
data class OpenRouterMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class OpenRouterResponse(
    val id: String?,
    val choices: List<OpenRouterChoice>?
)

@JsonClass(generateAdapter = true)
data class OpenRouterChoice(
    val message: OpenRouterMessageContent?,
    @Json(name = "finish_reason") val finishReason: String?
)

@JsonClass(generateAdapter = true)
data class OpenRouterMessageContent(
    val role: String?,
    val content: String?
)

interface OpenRouterApiService {
    @POST("chat/completions")
    suspend fun generateCompletion(
        @Header("Authorization") authHeader: String,
        @Header("HTTP-Referer") referer: String = "https://tubemaster.ai",
        @Header("X-Title") title: String = "TubeMaster AI",
        @Body request: OpenRouterRequest
    ): OpenRouterResponse
}

class OpenRouterProvider(
    customApiKey: String? = null,
    private val model: String = DEFAULT_MODEL
) : AIProvider {

    companion object {
        const val DEFAULT_MODEL = "google/gemini-2.5-flash"
        const val BACKUP_MODEL = "meta-llama/llama-3.3-70b-instruct"
    }

    private val effectiveApiKey: String = customApiKey?.ifBlank { null } ?: try {
        BuildConfig.OPENROUTER_API_KEY
    } catch (e: Throwable) {
        ""
    }

    private val apiService: OpenRouterApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenRouterApiService::class.java)
    }

    override suspend fun generate(
        tool: ToolConfig,
        inputs: Map<String, Any>,
        language: String
    ): Result<GenerationResult> = withContext(Dispatchers.IO) {
        try {
            if (effectiveApiKey.isBlank() || effectiveApiKey == "MY_OPENROUTER_API_KEY") {
                return@withContext Result.failure(IllegalStateException("OpenRouter API key is not configured"))
            }

            val systemPrompt = PromptBuilder.buildSystemPrompt(tool, language)
            val userPrompt = PromptBuilder.buildUserPrompt(tool, inputs, language)

            val request = OpenRouterRequest(
                model = model,
                messages = listOf(
                    OpenRouterMessage(role = "system", content = systemPrompt),
                    OpenRouterMessage(role = "user", content = userPrompt)
                )
            )

            val response = apiService.generateCompletion(
                authHeader = "Bearer $effectiveApiKey",
                request = request
            )

            val content = response.choices?.firstOrNull()?.message?.content
                ?: return@withContext Result.failure(IllegalStateException("Empty response received from OpenRouter"))

            val parsed = ResultParser.parse(tool, content)
            Result.success(parsed)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
