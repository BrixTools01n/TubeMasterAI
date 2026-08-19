package com.example.ai

import com.example.model.GenerationResult
import com.example.model.Platform
import com.example.model.ToolConfig
import com.example.model.ToolOutputType

object PromptBuilder {

    fun buildSystemPrompt(tool: ToolConfig, language: String): String {
        val platformContext = when (tool.platform) {
            Platform.YOUTUBE -> "You are an elite YouTube growth strategist, viral scriptwriter, and SEO algorithm master who has engineered millions of views across diverse niches."
            Platform.INSTAGRAM -> "You are a top Instagram viral architect, Reels producer, and organic reach specialist focused on high retention, shareability, and ManyChat comment conversion."
            Platform.FACEBOOK -> "You are a senior Meta and Facebook marketing expert, community building specialist, and high-converting ad copywriter."
        }

        val languageInstruction = when (language.lowercase()) {
            "hindi", "hi" -> "CRITICAL LANGUAGE MANDATE: You MUST generate the entire content strictly in Hindi (हिन्दी in Devanagari script) using engaging, natural creator vocabulary."
            "hinglish" -> "CRITICAL LANGUAGE MANDATE: You MUST generate the entire content in conversational Hinglish (natural mix of Hindi and English written in Latin script, exactly as Indian creators speak on Reels and YouTube, e.g. 'Ye 3 mistakes mat karna agar views badhane hain')."
            else -> "Generate the content in clear, punchy, high-retention English."
        }

        return """
            $platformContext
            
            Your mission: Execute the tool '${tool.name}' (${tool.category}) with unmatched quality.
            Description: ${tool.description}
            
            $languageInstruction
            
            OUTPUT FORMATTING RULES:
            - Provide ONLY the direct, high-value generated content.
            - Do NOT include conversational filler like 'Sure! Here are your titles:' or 'Hope this helps!'.
            - If generating a list, provide clean numbered items (e.g. 1. Title, 2. Title).
            - If generating tags or hashtags, provide them separated by commas or spaces.
            - If generating scripts, format cleanly with [HOOK], [BODY / B-ROLL], and [CTA] sections.
            - If generating a calendar or strategy, organize with clear headers (## Day 1, ## Week 1, etc.).
        """.trimIndent()
    }

    fun buildUserPrompt(tool: ToolConfig, inputs: Map<String, Any>, language: String): String {
        val sb = StringBuilder()
        sb.append("Execute tool '${tool.name}' with the following input parameters:\n\n")

        inputs.forEach { (key, value) ->
            if (key != "additional_instructions" && value.toString().isNotBlank()) {
                val fieldLabel = tool.fields.firstOrNull { it.id == key }?.label ?: key
                sb.append("• $fieldLabel: $value\n")
            }
        }

        // Custom User Instructions
        val customInstructions = inputs["additional_instructions"]?.toString()
        if (!customInstructions.isNullOrBlank()) {
            sb.append("\nCUSTOM USER INSTRUCTIONS (Apply these strictly):\n$customInstructions\n")
        }

        sb.append("\nTarget Output Language: $language\n")
        sb.append("Expected Output Type: ${tool.outputType.name}\n")
        sb.append("Generate maximum value content now:")

        return sb.toString()
    }
}

object ResultParser {
    fun parse(tool: ToolConfig, rawText: String): GenerationResult {
        val trimmed = rawText.trim()

        return when (tool.outputType) {
            ToolOutputType.LIST -> {
                val lines = trimmed.lines()
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .map { line ->
                        // Remove leading number if present (e.g. "1. ", "1) ", "- ")
                        line.replace(Regex("""^(\d+[\.\)]|\-|\*)\s*"""), "").trim()
                    }
                    .filter { it.isNotBlank() }

                GenerationResult(
                    toolId = tool.id,
                    toolName = tool.name,
                    platform = tool.platform,
                    outputType = ToolOutputType.LIST,
                    rawText = trimmed,
                    items = if (lines.isNotEmpty()) lines else listOf(trimmed)
                )
            }

            ToolOutputType.TAGS -> {
                // Split by commas, newlines or spaces if hashtags
                val tags = if (trimmed.contains(",")) {
                    trimmed.split(",").map { it.trim().removePrefix("#").trim() }.filter { it.isNotBlank() }
                } else if (trimmed.contains("#")) {
                    Regex("""#[\w\d_-]+""").findAll(trimmed).map { it.value.removePrefix("#") }.toList()
                } else {
                    trimmed.lines().map { it.trim() }.filter { it.isNotBlank() }
                }

                GenerationResult(
                    toolId = tool.id,
                    toolName = tool.name,
                    platform = tool.platform,
                    outputType = ToolOutputType.TAGS,
                    rawText = trimmed,
                    tags = if (tags.isNotEmpty()) tags else listOf(trimmed)
                )
            }

            ToolOutputType.SCRIPT, ToolOutputType.STRATEGY, ToolOutputType.KEY_VALUE -> {
                val sections = linkedMapOf<String, String>()
                val lines = trimmed.lines()
                var currentHeading = "Overview"
                val currentContent = StringBuilder()

                for (line in lines) {
                    val isHeader = line.startsWith("##") || line.startsWith("#") ||
                            (line.startsWith("[") && line.endsWith("]")) ||
                            (line.endsWith(":") && line.length < 50 && !line.startsWith("http"))

                    if (isHeader) {
                        if (currentContent.isNotBlank()) {
                            sections[currentHeading] = currentContent.toString().trim()
                            currentContent.clear()
                        }
                        currentHeading = line.replace("#", "").replace("[", "").replace("]", "").removeSuffix(":").trim()
                    } else {
                        currentContent.append(line).append("\n")
                    }
                }

                if (currentContent.isNotBlank()) {
                    sections[currentHeading] = currentContent.toString().trim()
                }

                GenerationResult(
                    toolId = tool.id,
                    toolName = tool.name,
                    platform = tool.platform,
                    outputType = tool.outputType,
                    rawText = trimmed,
                    sections = sections
                )
            }

            ToolOutputType.TEXT -> {
                GenerationResult(
                    toolId = tool.id,
                    toolName = tool.name,
                    platform = tool.platform,
                    outputType = ToolOutputType.TEXT,
                    rawText = trimmed
                )
            }
        }
    }
}
