package com.example.data.registry

import com.example.data.local.ToolOverrideEntity
import com.example.model.Platform
import com.example.model.ToolConfig

object ToolRegistry {
    val youtubeTools: List<ToolConfig> = YouTubeTools.list
    val instagramTools: List<ToolConfig> = InstagramTools.list
    val facebookTools: List<ToolConfig> = FacebookTools.list

    val allTools: List<ToolConfig> = youtubeTools + instagramTools + facebookTools

    init {
        // Programmatic validation to enforce exact tool count mandates
        check(youtubeTools.size == 40) { "YouTube tools count must be exactly 40, found: ${youtubeTools.size}" }
        check(instagramTools.size == 30) { "Instagram tools count must be exactly 30, found: ${instagramTools.size}" }
        check(facebookTools.size == 30) { "Facebook tools count must be exactly 30, found: ${facebookTools.size}" }
        check(allTools.size == 100) { "Total tools count must be exactly 100, found: ${allTools.size}" }
    }

    val totalCount: Int get() = allTools.size
    val youtubeCount: Int get() = youtubeTools.size
    val instagramCount: Int get() = instagramTools.size
    val facebookCount: Int get() = facebookTools.size

    fun applyOverrides(tools: List<ToolConfig>, overrides: Map<String, ToolOverrideEntity>): List<ToolConfig> {
        return tools.mapNotNull { tool ->
            val override = overrides[tool.id]
            if (override != null && (override.isDeleted || override.isDisabled)) {
                // If soft-deleted or disabled, omit from normal browsing (or return with flag)
                null
            } else if (override != null && override.isProOverride != null) {
                tool.copy(isPro = override.isProOverride)
            } else {
                tool
            }
        }
    }

    fun getToolById(id: String, overrides: Map<String, ToolOverrideEntity> = emptyMap()): ToolConfig? {
        val base = allTools.find { it.id == id } ?: return null
        val override = overrides[id]
        if (override != null && override.isProOverride != null) {
            return base.copy(isPro = override.isProOverride)
        }
        return base
    }

    fun getToolsForPlatform(platform: Platform?, overrides: Map<String, ToolOverrideEntity> = emptyMap()): List<ToolConfig> {
        val base = when (platform) {
            Platform.YOUTUBE -> youtubeTools
            Platform.INSTAGRAM -> instagramTools
            Platform.FACEBOOK -> facebookTools
            null -> allTools
        }
        return if (overrides.isEmpty()) base else applyOverrides(base, overrides)
    }

    fun getCategoriesForPlatform(platform: Platform?, overrides: Map<String, ToolOverrideEntity> = emptyMap()): List<String> {
        val tools = getToolsForPlatform(platform, overrides)
        return listOf("All") + tools.map { it.category }.distinct()
    }

    fun searchTools(
        query: String,
        platform: Platform? = null,
        category: String? = null,
        overrides: Map<String, ToolOverrideEntity> = emptyMap()
    ): List<ToolConfig> {
        val base = getToolsForPlatform(platform, overrides)
        val categoryFiltered = if (category != null && category != "All") {
            base.filter { it.category.equals(category, ignoreCase = true) }
        } else {
            base
        }

        if (query.isBlank()) return categoryFiltered

        val cleanQuery = query.trim().lowercase()
        return categoryFiltered.filter { tool ->
            tool.name.lowercase().contains(cleanQuery) ||
            tool.description.lowercase().contains(cleanQuery) ||
            tool.category.lowercase().contains(cleanQuery) ||
            tool.platform.displayName.lowercase().contains(cleanQuery) ||
            tool.keywords.any { it.lowercase().contains(cleanQuery) }
        }
    }

    // Popular / Featured tools for the Home Dashboard
    val popularTools: List<ToolConfig> by lazy {
        listOf(
            getToolById("yt_viral_title") ?: youtubeTools[0],
            getToolById("yt_description") ?: youtubeTools[2],
            getToolById("yt_tags") ?: youtubeTools[3],
            getToolById("yt_hashtag") ?: youtubeTools[4],
            getToolById("ig_caption") ?: instagramTools[0],
            getToolById("ig_reels_script") ?: instagramTools[2],
            getToolById("ig_reels_hook") ?: instagramTools[3],
            getToolById("ig_bio") ?: instagramTools[1],
            getToolById("fb_post") ?: facebookTools[0],
            getToolById("fb_reel_script") ?: facebookTools[4]
        )
    }
}
