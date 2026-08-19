package com.example.ai

import com.example.BuildConfig
import com.example.model.GenerationResult
import com.example.model.ToolConfig
import com.example.model.ToolOutputType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class GeminiAIProvider : AIProvider {

    private val apiService: GeminiApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        retrofit.create(GeminiApiService::class.java)
    }

    override suspend fun generate(
        tool: ToolConfig,
        inputs: Map<String, Any>,
        language: String
    ): Result<GenerationResult> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "YOUR_API_KEY") {
            return@withContext Result.failure(IllegalStateException("API_KEY_NOT_CONFIGURED"))
        }

        val prompt = buildPrompt(tool, inputs, language)
        val systemInstruction = "You are TubeMaster AI, a world-class creator productivity and social media growth engine for YouTube, Instagram, and Facebook. Produce clear, high-CTR, actionable, and formatted output tailored to the creator's exact inputs. Do not include meta commentary or conversational filler."

        val request = GeminiGenerateRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = prompt))
                )
            ),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.7f,
                topP = 0.95f,
                topK = 40,
                maxOutputTokens = 2048
            ),
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = systemInstruction))
            )
        )

        try {
            val response = apiService.generateContent(apiKey, request)
            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response received from Gemini API")

            val parsedResult = parseResponse(tool, responseText)
            Result.success(parsedResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildPrompt(tool: ToolConfig, inputs: Map<String, Any>, language: String): String {
        val sb = StringBuilder()
        sb.append("TASK: Generate content for the tool '${tool.name}' on platform '${tool.platform.displayName}' in category '${tool.category}'.\n")
        sb.append("Language: $language\n\n")
        sb.append("USER INPUTS:\n")
        inputs.forEach { (key, value) ->
            sb.append("- $key: $value\n")
        }
        sb.append("\nINSTRUCTIONS FOR FORMATTING:\n")
        when (tool.outputType) {
            ToolOutputType.LIST -> {
                sb.append("Output exactly as a numbered list (1., 2., 3., etc.). Provide each distinct option clearly.")
            }
            ToolOutputType.TAGS -> {
                sb.append("Output the tags or hashtags separated by spaces or commas. Ensure high relevance and trending potential.")
            }
            ToolOutputType.SCRIPT -> {
                sb.append("Output formatted with clear scene headings in brackets like [HOOK], [INTRO & PROBLEM], [MAIN CONTENT - STEP 1], [MAIN CONTENT - STEP 2], [CTA & OUTRO], along with speaker dialogue and [Visual / B-Roll Cue] notes.")
            }
            ToolOutputType.TEXT -> {
                sb.append("Output a complete, formatted piece of copy ready to paste into the platform, with suitable line breaks and structure.")
            }
            ToolOutputType.STRATEGY -> {
                sb.append("Output structured sections with clear headings and bullet points for strategic execution.")
            }
            ToolOutputType.KEY_VALUE -> {
                sb.append("Output organized key-value analysis with metric names and detailed recommendations.")
            }
        }
        return sb.toString()
    }

    private fun parseResponse(tool: ToolConfig, text: String): GenerationResult {
        val cleanText = text.trim()
        val lines = cleanText.lines().map { it.trim() }.filter { it.isNotEmpty() }

        var items = emptyList<String>()
        var tags = emptyList<String>()
        val sections = linkedMapOf<String, String>()

        when (tool.outputType) {
            ToolOutputType.LIST -> {
                items = lines.filter { line ->
                    line.matches(Regex("""^(\d+[\.\)]|\-|\*|•)\s+.*"""))
                }.map { line ->
                    line.replace(Regex("""^(\d+[\.\)]|\-|\*|•)\s+"""), "").trim()
                }
                if (items.isEmpty()) {
                    items = lines
                }
            }
            ToolOutputType.TAGS -> {
                tags = if (cleanText.contains("#")) {
                    cleanText.split(Regex("""\s+""")).filter { it.startsWith("#") }
                } else {
                    cleanText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                }
            }
            ToolOutputType.SCRIPT, ToolOutputType.STRATEGY, ToolOutputType.KEY_VALUE -> {
                var currentHeading = "Overview"
                val currentBody = StringBuilder()

                for (line in lines) {
                    if (line.startsWith("#") || line.startsWith("[") || (line.contains(":") && line.length < 50 && !line.startsWith("http"))) {
                        if (currentBody.isNotEmpty()) {
                            sections[currentHeading] = currentBody.toString().trim()
                            currentBody.clear()
                        }
                        currentHeading = line.replace(Regex("""^[#\[\]]+"""), "").trim()
                    } else {
                        currentBody.append(line).append("\n")
                    }
                }
                if (currentBody.isNotEmpty()) {
                    sections[currentHeading] = currentBody.toString().trim()
                }
            }
            ToolOutputType.TEXT -> {
                // Keep rawText as is
            }
        }

        return GenerationResult(
            toolId = tool.id,
            toolName = tool.name,
            platform = tool.platform,
            outputType = tool.outputType,
            rawText = cleanText,
            items = items,
            sections = sections,
            tags = tags
        )
    }
}
