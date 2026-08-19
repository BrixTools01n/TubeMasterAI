package com.example

import com.example.ai.MockAIProvider
import com.example.data.registry.FacebookTools
import com.example.data.registry.InstagramTools
import com.example.data.registry.ToolRegistry
import com.example.data.registry.YouTubeTools
import com.example.model.Platform
import com.example.model.ToolOutputType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TubeMasterCUJTest {

    @Test
    fun testToolRegistryCounts() {
        assertEquals("YouTube tools count must be exactly 40", 40, YouTubeTools.list.size)
        assertEquals("Instagram tools count must be exactly 30", 30, InstagramTools.list.size)
        assertEquals("Facebook tools count must be exactly 30", 30, FacebookTools.list.size)
        assertEquals("Total tools count must be exactly 100", 100, ToolRegistry.allTools.size)
    }

    @Test
    fun testSearchAndFilter() {
        // Search across all
        val titleResults = ToolRegistry.searchTools("Title")
        assertTrue("Search for 'Title' should return tools", titleResults.isNotEmpty())

        // Filter by platform
        val ytTools = ToolRegistry.getToolsForPlatform(Platform.YOUTUBE)
        assertEquals(40, ytTools.size)

        val igTools = ToolRegistry.getToolsForPlatform(Platform.INSTAGRAM)
        assertEquals(30, igTools.size)

        val fbTools = ToolRegistry.getToolsForPlatform(Platform.FACEBOOK)
        assertEquals(30, fbTools.size)

        // Filter by category
        val seoTools = ToolRegistry.searchTools("", Platform.YOUTUBE, "SEO & Discovery")
        assertEquals(10, seoTools.size)
    }

    @Test
    fun testMockAIEngineOutputs() = runBlocking {
        val mockProvider = MockAIProvider()

        // 1. Test List Output (e.g. Viral Title Generator)
        val viralTitleTool = ToolRegistry.getToolById("yt_viral_title")
        assertNotNull(viralTitleTool)
        val titleResult = mockProvider.generate(
            tool = viralTitleTool!!,
            inputs = mapOf("topic" to "AI Coding Assistants in 2026", "count" to 5f)
        ).getOrThrow()
        assertEquals(ToolOutputType.LIST, titleResult.outputType)
        assertTrue("List output should have generated items", titleResult.items.isNotEmpty())

        // 2. Test Tags Output (e.g. YouTube Tags Generator)
        val tagsTool = ToolRegistry.getToolById("yt_tags")
        assertNotNull(tagsTool)
        val tagsResult = mockProvider.generate(
            tool = tagsTool!!,
            inputs = mapOf("topic" to "NextJS Full Course", "niche" to "Web Dev")
        ).getOrThrow()
        assertEquals(ToolOutputType.TAGS, tagsResult.outputType)
        assertTrue("Tags output should contain comma or tags", tagsResult.tags.isNotEmpty())

        // 3. Test Script Output (e.g. Script Generator)
        val scriptTool = ToolRegistry.getToolById("yt_script_generator")
        assertNotNull(scriptTool)
        val scriptResult = mockProvider.generate(
            tool = scriptTool!!,
            inputs = mapOf("topic" to "Top 5 Productivity Habits")
        ).getOrThrow()
        assertEquals(ToolOutputType.SCRIPT, scriptResult.outputType)
        assertTrue("Script output should contain structured sections", scriptResult.sections.isNotEmpty())

        // 4. Test Strategy Output (e.g. Content Calendar)
        val calendarTool = ToolRegistry.getToolById("yt_content_calendar")
        assertNotNull(calendarTool)
        val calendarResult = mockProvider.generate(
            tool = calendarTool!!,
            inputs = mapOf("niche" to "Fitness")
        ).getOrThrow()
        assertEquals(ToolOutputType.STRATEGY, calendarResult.outputType)
        assertTrue("Strategy output should have sections", calendarResult.sections.isNotEmpty())
    }

    @Test
    fun testAuthStateEnumValues() {
        val states = com.example.data.local.AuthState.values()
        assertEquals(3, states.size)
        assertTrue(states.contains(com.example.data.local.AuthState.LOADING))
        assertTrue(states.contains(com.example.data.local.AuthState.AUTHENTICATED))
        assertTrue(states.contains(com.example.data.local.AuthState.UNAUTHENTICATED))
    }

    @Test
    fun testAuthMeResultStructure() {
        val unauthenticatedResult = com.example.data.local.AuthMeResult(authenticated = false)
        assertEquals(false, unauthenticatedResult.authenticated)
        assertEquals(null, unauthenticatedResult.user)

        val authenticatedResult = com.example.data.local.AuthMeResult(
            authenticated = true,
            user = com.example.data.local.UserEntity(
                id = "test-user-1",
                name = "Test Creator",
                email = "test@tubemaster.ai",
                provider = "google",
                passwordHash = "",
                role = "user",
                plan = "free",
                subscriptionStatus = "FREE",
                generationCount = 0,
                limitReachedAt = null,
                isSuspended = false,
                createdAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )
        )
        assertEquals(true, authenticatedResult.authenticated)
        assertNotNull(authenticatedResult.user)
        assertEquals("test@tubemaster.ai", authenticatedResult.user?.email)
    }
}
