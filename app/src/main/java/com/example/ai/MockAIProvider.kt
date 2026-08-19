package com.example.ai

import com.example.model.GenerationResult
import com.example.model.Platform
import com.example.model.ToolConfig
import com.example.model.ToolOutputType
import kotlinx.coroutines.delay

class MockAIProvider : AIProvider {

    override suspend fun generate(
        tool: ToolConfig,
        inputs: Map<String, Any>,
        language: String
    ): Result<GenerationResult> {
        // Realistic simulated processing delay for UX polish
        delay(900)

        val topic = inputs["topic"]?.toString()
            ?: inputs["main_topic"]?.toString()
            ?: inputs["title"]?.toString()
            ?: inputs["video_title"]?.toString()
            ?: inputs["seed_keyword"]?.toString()
            ?: inputs["niche"]?.toString()
            ?: "Content Creation & Growth"

        val niche = inputs["niche"]?.toString() ?: "Creator Economy"
        val count = (inputs["count"] as? Float)?.toInt() ?: 5

        val result = when (tool.outputType) {
            ToolOutputType.LIST -> generateMockList(tool, topic, niche, count, language)
            ToolOutputType.TAGS -> generateMockTags(tool, topic, niche, count)
            ToolOutputType.SCRIPT -> generateMockScript(tool, topic, niche, language)
            ToolOutputType.TEXT -> generateMockText(tool, topic, niche, inputs, language)
            ToolOutputType.STRATEGY -> generateMockStrategy(tool, topic, niche, language)
            ToolOutputType.KEY_VALUE -> generateMockKeyValue(tool, topic, inputs)
        }

        return Result.success(result)
    }

    private fun generateMockList(
        tool: ToolConfig,
        topic: String,
        niche: String,
        count: Int,
        language: String
    ): GenerationResult {
        val items = when (tool.id) {
            "yt_viral_title" -> listOf(
                "I Tested $topic for 30 Days (Here's What Happened)",
                "Why 99% of People Fail at $topic (Avoid This)",
                "The $topic Secret Nobody Is Telling You",
                "Stop Doing $topic Like This in 2026!",
                "$topic: From $0 to Mastery (Full Guide)",
                "I Regret Not Knowing This About $topic Earlier...",
                "The Truth About $topic (Exposed with Proof)",
                "How I Mastered $topic in 24 Hours (Step-by-Step)"
            )
            "yt_seo_title" -> listOf(
                "$topic Tutorial for Beginners (2026 Step-by-Step Guide)",
                "How to Master $topic | Complete Walkthrough & Best Practices",
                "$topic Explained: Everything You Need to Know",
                "Top 7 $topic Strategies to Rank #1 on YouTube",
                "$topic Masterclass: Tips, Tools & Frameworks"
            )
            "yt_video_idea", "yt_viral_idea" -> listOf(
                "Spending 24 Hours Living Exclusively with $topic",
                "Extreme $topic Challenge: Budget Edition vs $10,000 Setup",
                "Testing Every Viral $topic Hack So You Don't Have To",
                "Why Everyone Is Wrong About $topic in 2026",
                "I Paid a Top Expert on Fiverr to Critique My $topic",
                "The Untold Dark Side of $topic (Deep Dive)",
                "Building the Ultimate $topic System from Scratch"
            )
            "yt_hook_generator" -> listOf(
                "\"If you are still struggling with $topic, pause this video right now.\"",
                "\"99% of people get $topic completely backwards. Here is what actually works.\"",
                "\"What if I told you that one single tweak in your $topic could 10x your results?\"",
                "\"I wasted 6 months doing $topic the hard way until I found this trick.\"",
                "\"Watch this before you spend another dollar on $topic.\""
            )
            "ig_bio", "yt_bio_generator", "fb_bio" -> listOf(
                "🚀 Helping you master $topic without the overwhelm\n✨ Top-rated creator in $niche\n👇 Grab free starter toolkit below",
                "💡 Simplifies $topic for ambitious creators\n📈 100k+ community | Daily breakdowns\n🔗 Watch free masterclass:",
                "🎯 $topic made simple & actionable\n🔥 Real experiments, zero fluff\n📬 Join 25,000+ newsletter readers:"
            )
            "ig_reels_hook" -> listOf(
                "🛑 Stop scrolling if you want to master $topic faster.",
                "👀 This one $topic secret feels almost illegal to know.",
                "🤫 Nobody is talking about this hidden $topic strategy...",
                "⚠️ If you make this $topic mistake, you're leaving money on the table.",
                "🔥 3 things I wish I knew before starting $topic in 2026."
            )
            else -> listOf(
                "High Impact $topic Strategy for $niche",
                "Top 5 $topic Principles Every Creator Must Follow",
                "How to Leverage $topic for 3x Engagement",
                "The Psychology Behind $topic Success in 2026",
                "Common $topic Pitfalls and How to Avoid Them"
            )
        }.take(count.coerceAtLeast(3))

        val formattedText = items.mapIndexed { idx, it -> "${idx + 1}. $it" }.joinToString("\n\n")

        return GenerationResult(
            toolId = tool.id,
            toolName = tool.name,
            platform = tool.platform,
            outputType = tool.outputType,
            rawText = formattedText,
            items = items
        )
    }

    private fun generateMockTags(
        tool: ToolConfig,
        topic: String,
        niche: String,
        count: Int
    ): GenerationResult {
        val cleanTopic = topic.replace(" ", "").lowercase()
        val cleanNiche = niche.replace(" ", "").lowercase()

        val isHashtag = tool.id.contains("hashtag")
        val prefix = if (isHashtag) "#" else ""

        val rawTags = listOf(
            "${prefix}$cleanTopic",
            "${prefix}${cleanTopic}tips",
            "${prefix}${cleanTopic}tutorial",
            "${prefix}${cleanTopic}2026",
            "${prefix}${cleanTopic}guide",
            "${prefix}$cleanNiche",
            "${prefix}${cleanNiche}creator",
            "${prefix}contentcreator",
            "${prefix}growthhacks",
            "${prefix}viralvideos",
            "${prefix}creatortools",
            "${prefix}socialmediatips",
            "${prefix}${cleanTopic}hacks",
            "${prefix}digitalgrowth",
            "${prefix}youtubegrowth",
            "${prefix}instagramgrowth",
            "${prefix}trendingnow",
            "${prefix}reelsvideo",
            "${prefix}shortsvideo",
            "${prefix}algorithmsecrets"
        ).take(count.coerceAtLeast(8))

        val joined = if (isHashtag) rawTags.joinToString(" ") else rawTags.joinToString(", ")

        return GenerationResult(
            toolId = tool.id,
            toolName = tool.name,
            platform = tool.platform,
            outputType = tool.outputType,
            rawText = joined,
            tags = rawTags
        )
    }

    private fun generateMockScript(
        tool: ToolConfig,
        topic: String,
        niche: String,
        language: String
    ): GenerationResult {
        val sections = linkedMapOf(
            "HOOK (0:00 - 0:05)" to "[Visual: Quick jump cut into camera with high-energy expression]\n\"If you are trying to succeed with $topic in 2026, stop making this one costly mistake.\"",
            "PROBLEM & STAKES (0:05 - 0:20)" to "[Visual: Screen recording / B-roll of frustrating search results]\n\"Most creators spend weeks overcomplicating their workflow, getting zero traction. But here is what the top 1% in $niche do differently.\"",
            "STEP 1: THE CORE SYSTEM (0:20 - 0:45)" to "[Visual: On-screen diagram / dynamic bullet points overlay]\n\"First, simplify your input pipeline. Focus only on the highest-leverage 20% that produces 80% of your engagement results.\"",
            "STEP 2: RETENTION & EXECUTION (0:45 - 1:15)" to "[Visual: Demonstrating tool or direct actionable demonstration]\n\"Next, package your content with clear pacing. Use visual pattern interrupts every 4 to 6 seconds to keep retention above 70%.\"",
            "CALL TO ACTION & OUTRO (1:15 - 1:30)" to "[Visual: Friendly smile with point to screen and subscribe badge animation]\n\"If you found this helpful, hit the like button and subscribe for daily $niche masterclasses. What is your #1 question on $topic? Drop it in the comments below!\""
        )

        val fullScript = sections.entries.joinToString("\n\n") { (heading, body) ->
            "📍 $heading\n$body"
        }

        return GenerationResult(
            toolId = tool.id,
            toolName = tool.name,
            platform = tool.platform,
            outputType = tool.outputType,
            rawText = fullScript,
            sections = sections
        )
    }

    private fun generateMockText(
        tool: ToolConfig,
        topic: String,
        niche: String,
        inputs: Map<String, Any>,
        language: String
    ): GenerationResult {
        val content = when (tool.id) {
            "yt_description" -> """
                🔥 In this complete masterclass on $topic, we break down the exact blueprint you need to get measurable results in 2026.

                Whether you're just starting in $niche or looking to optimize your existing workflow, this step-by-step tutorial will save you hundreds of wasted hours.

                ⏱️ TIMESTAMPS:
                0:00 - Why Most People Get $topic Wrong
                1:30 - The 3 Core Pillars You Must Know
                4:45 - Step-by-Step Practical Demonstration
                8:20 - Advanced Hacks for Maximum Efficiency
                11:10 - Common Mistakes & How to Avoid Them
                13:40 - Final Action Plan & Free Resources

                🔗 RESOURCES & LINKS MENTIONED:
                ▸ Free Creator Growth Checklist: https://tubemaster.ai/free-guide
                ▸ Join our Private Creator Community: https://tubemaster.ai/community
                ▸ Gear & Tools Used in this Video: https://tubemaster.ai/kit

                💬 QUESTION OF THE DAY:
                What is your biggest obstacle when working on $topic? Let me know in the comments!

                🔔 Subscribe for new in-depth tutorials every week: https://youtube.com/@TubeMasterAI?sub_confirmation=1

                #$niche #$topic #CreatorEconomy #Tutorial2026
            """.trimIndent()

            "ig_caption" -> """
                The harsh truth about $topic nobody wants to admit... 👇

                3 years ago, I thought success in $niche came down to working 16 hours a day and relying on luck.

                I was wrong. 🛑

                The breakthrough happened when I stopped chasing every trend and focused on ONE core system:
                
                1️⃣ Consistent quality over random quantity
                2️⃣ Obsessive audience research
                3️⃣ Clear, friction-free value in every post

                Save this post so you don't forget these principles next time you create! 📌

                Drop a '🔥' in the comments if you agree, or let me know your thoughts below!

                .
                .
                #$niche #$topic #ContentCreator #MindsetShift #InstagramGrowth
            """.trimIndent()

            "fb_post" -> """
                I made a huge realization this week regarding $topic.

                Too often in $niche, we get trapped doing things the "traditional" way because everyone else does. But when you look at actual data, the rules have changed completely.

                Here are 3 rules I now follow religiously:

                1. Clarity always beats cleverness.
                2. If it doesn't solve a real problem for the reader, don't post it.
                3. Engagement is a two-way street — reply to every genuine comment.

                What has been your experience with $topic lately? Have you noticed this shift as well?

                Let's discuss in the comments below! 👇
            """.trimIndent()

            "yt_sponsorship_pitch", "ig_brand_deal_pitch" -> """
                Subject: Collaboration Proposal: TubeMaster AI x [Brand Name] ($niche Audience)

                Hi [Brand Partnership Team],

                I hope you are having a productive week!

                My name is [Creator Name], and I create high-engagement content focused on $niche with an active community of over 45,000+ dedicated creators.

                Our audience is deeply invested in $topic and actively looks for recommendations on reliable products in this space.

                I’ve been a long-time admirer of [Brand Name] and believe your platform would deliver immense value to our viewers. I would love to explore a dedicated integration or product showcase in an upcoming video.

                Our recent collaborations averaged over 85,000+ views with an organic 7.8% engagement rate.

                Would you be open to a brief conversation this week to discuss custom partnership packages?

                Looking forward to connecting!

                Warm regards,
                [Creator Name]
                Media Kit & Stats: https://tubemaster.ai/mediakit
            """.trimIndent()

            else -> """
                ✨ $topic Optimization Guide for $niche:

                • Core Objective: Maximize engagement, reach, and conversion for $topic.
                • Target Demographic: Active digital creators and audience interested in $niche.
                • Recommended Action: Deploy content with clear pattern interrupts, strong emotional hooks, and high-contrast visuals.
                • Measurement Metric: Target at least 65% average retention and 5%+ comment interaction rate.
            """.trimIndent()
        }

        return GenerationResult(
            toolId = tool.id,
            toolName = tool.name,
            platform = tool.platform,
            outputType = tool.outputType,
            rawText = content
        )
    }

    private fun generateMockStrategy(
        tool: ToolConfig,
        topic: String,
        niche: String,
        language: String
    ): GenerationResult {
        val sections = linkedMapOf(
            "PHASE 1: AUDIENCE & POSITIONING" to "Target persona: Ambitious creators seeking clarity in $niche.\nCore Value Prop: Breaking down $topic into actionable, fluff-free frameworks.",
            "PHASE 2: CONTENT PILLARS (70/20/10 RULE)" to "• 70% Search & Authority: Deep dive tutorials on $topic\n• 20% Viral & Trend Surfing: Quick tips, Shorts/Reels & Reaction videos\n• 10% Community & Behind-the-Scenes: Personal stories, failures, and milestone updates",
            "PHASE 3: 30-DAY EXECUTION SCHEDULE" to "• Week 1: Foundational $topic Pillar Guide + 3 Micro Shorts\n• Week 2: Case Study Breakdown with Real Data\n• Week 3: Common Myths Exposed in $niche\n• Week 4: Community Q&A + Live Troubleshooting Session",
            "PHASE 4: MONETIZATION & CONVERSION" to "• Primary Lead Magnet: Free $topic Resource Checklist in Bio\n• Middle Funnel: Weekly VIP Creator Newsletter\n• Back-End Offer: 1-on-1 Consultation & Premium Creator Mastermind"
        )

        val fullText = sections.entries.joinToString("\n\n") { (k, v) ->
            "📊 $k\n$v"
        }

        return GenerationResult(
            toolId = tool.id,
            toolName = tool.name,
            platform = tool.platform,
            outputType = tool.outputType,
            rawText = fullText,
            sections = sections
        )
    }

    private fun generateMockKeyValue(
        tool: ToolConfig,
        topic: String,
        inputs: Map<String, Any>
    ): GenerationResult {
        val pairs = when (tool.id) {
            "yt_seo_score" -> mapOf(
                "Overall SEO Score" to "92 / 100 (Excellent)",
                "Title Click-Through Potential" to "High (Contains curiosity gap & keyword)",
                "Keyword Density" to "Optimal (3.4% in first 200 words)",
                "Search Intent Alignment" to "95% match for searcher queries",
                "Actionable Improvement" to "Add 2 high-volume long-tail tags in the first paragraph"
            )
            "yt_ab_titles" -> mapOf(
                "Variant A (Curiosity & Emotion)" to "I Tried $topic for 30 Days and I'm Shocked",
                "Variant B (Direct Benefit & Numbers)" to "How to Master $topic: 5 Proven Steps for 2026",
                "Variant C (Fear of Missing Out / Warning)" to "The #1 $topic Mistake That Destroys Your Channel"
            )
            "yt_upload_schedule" -> mapOf(
                "Best Days to Upload" to "Tuesday, Thursday & Sunday",
                "Optimal Time Slot (EST)" to "2:00 PM – 5:00 PM EST (Peak viewer window)",
                "Primary Audience Peak" to "Evenings & Weekends",
                "Shorts Publishing Velocity" to "Daily at 11:00 AM & 6:30 PM EST"
            )
            else -> mapOf(
                "Primary Search Intent" to "Educational & Problem Solving",
                "Target Audience Level" to "Beginner to Intermediate",
                "Competition Index" to "Medium-Low (Great opportunity to rank)",
                "Estimated CPM Tier" to "$8.50 - $14.20 per 1,000 views"
            )
        }

        val fullText = pairs.entries.joinToString("\n\n") { (k, v) ->
            "🔹 $k:\n$v"
        }

        return GenerationResult(
            toolId = tool.id,
            toolName = tool.name,
            platform = tool.platform,
            outputType = tool.outputType,
            rawText = fullText,
            sections = pairs
        )
    }
}
