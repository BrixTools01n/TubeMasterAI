package com.example.data.registry

import com.example.model.InputField
import com.example.model.Platform
import com.example.model.ToolConfig
import com.example.model.ToolOutputType

object InstagramTools {
    val list: List<ToolConfig> = listOf(
        // === Instagram Content (1-10) ===
        ToolConfig(
            id = "ig_caption",
            name = "Instagram Caption Generator",
            platform = Platform.INSTAGRAM,
            category = "Content",
            description = "Scroll-stopping captions with catchy hooks, story body, and engaging calls to action.",
            iconKey = "caption",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("caption", "hook", "story", "engagement"),
            fields = listOf(
                InputField.Text("topic", "Post Topic / Picture Context", "e.g., Launching my new digital marketing agency"),
                InputField.Select("tone", "Caption Tone", listOf("Inspirational & Storytelling", "Witty & Casual", "Educational / How-To", "Bold & Controversial", "Short & Minimal")),
                InputField.Toggle("include_emojis", "Include Strategic Emojis", defaultValue = true),
                InputField.Toggle("include_hashtags", "Include 5 Niche Hashtags at End", defaultValue = true)
            )
        ),
        ToolConfig(
            id = "ig_bio",
            name = "Instagram Bio Generator",
            platform = Platform.INSTAGRAM,
            category = "Content",
            description = "Profile bios that convert profile visitors into followers with clear value and link CTA.",
            iconKey = "user",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("bio", "profile", "headline", "conversion"),
            fields = listOf(
                InputField.Text("name_or_brand", "Name or Handle", "e.g., Sarah Jenkins | Fitness Coach"),
                InputField.Text("value_prop", "What You Help People Do", "e.g., Helping busy moms lose 10 lbs without crazy diets"),
                InputField.Text("link_cta", "Freebie or Link Goal", "e.g., Grab free 7-day meal plan below 👇"),
                InputField.Select("style", "Layout Style", listOf("Bullet Points with Emojis", "Minimalist 2-Liner", "Authority & Accolades", "Fun & Quirky"))
            )
        ),
        ToolConfig(
            id = "ig_reels_script",
            name = "Reels Script Generator",
            platform = Platform.INSTAGRAM,
            category = "Content",
            description = "Full 30-60 second Reels scripts complete with visual cues, captions, and viral pacing.",
            iconKey = "reels",
            isPro = false,
            outputType = ToolOutputType.SCRIPT,
            keywords = listOf("reels", "script", "video", "tiktok"),
            fields = listOf(
                InputField.Text("topic", "Reels Topic", "e.g., 3 apps you must delete immediately for better focus"),
                InputField.Select("duration", "Length", listOf("15-30s (Fast & Snappy)", "30-60s (Educational Breakdown)", "60-90s (Full Story)")),
                InputField.Select("hook_type", "Hook Style", listOf("Visual Problem + Text Overlay", "Secret / Hack Revelation", "Don't Do This Mistake", "Relatable Skit"))
            )
        ),
        ToolConfig(
            id = "ig_reels_hook",
            name = "Reels Hook Generator",
            platform = Platform.INSTAGRAM,
            category = "Content",
            description = "First 3-second visual and audio hooks that stop the endless scroll instantly.",
            iconKey = "hook",
            isPro = true,
            outputType = ToolOutputType.LIST,
            keywords = listOf("hook", "reels", "stop scroll", "first 3 seconds"),
            fields = listOf(
                InputField.Text("topic", "Reel Topic / Message", "e.g., Why most people never build wealth"),
                InputField.Select("format", "Reels Format", listOf("Talking Head Video", "B-roll with Text Overlay", "Voiceover Tutorial", "POV / Green Screen")),
                InputField.Slider("count", "Hooks to Generate", min = 4f, max = 10f, defaultValue = 6f)
            )
        ),
        ToolConfig(
            id = "ig_reel_idea",
            name = "Reel Idea Generator",
            platform = Platform.INSTAGRAM,
            category = "Content",
            description = "10 actionable Reels concepts designed to tap into trending audio and relatable humor.",
            iconKey = "idea",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("reel ideas", "trending", "audio", "concepts"),
            fields = listOf(
                InputField.Text("niche", "Your Instagram Niche", "e.g., Real Estate, Graphic Design, Fashion"),
                InputField.Select("style", "Style Focus", listOf("Educational Tips / Carousels as Reels", "Relatable / Industry Humor", "Behind the Scenes Workflow", "Transformation Before & After"))
            )
        ),
        ToolConfig(
            id = "ig_carousel_idea",
            name = "Carousel Idea Generator",
            platform = Platform.INSTAGRAM,
            category = "Content",
            description = "High-save, high-share 7-10 slide carousel concepts that establish thought leadership.",
            iconKey = "carousel",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("carousel", "slides", "save", "share"),
            fields = listOf(
                InputField.Text("niche", "Niche / Industry", "e.g., UX Design & Freelancing"),
                InputField.Select("carousel_type", "Carousel Framework", listOf("Step-by-Step Tutorial", "Mistakes to Avoid", "Resource / Tool Listicle", "Case Study Breakdown"))
            )
        ),
        ToolConfig(
            id = "ig_carousel_caption",
            name = "Carousel Caption Generator",
            platform = Platform.INSTAGRAM,
            category = "Content",
            description = "Slide-by-slide text layout plus accompanying feed caption for maximum bookmark saves.",
            iconKey = "document",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("carousel slides", "slide text", "swipe", "caption"),
            fields = listOf(
                InputField.Text("topic", "Carousel Headline", "e.g., 5 Psychology Tricks Used by Apple"),
                InputField.Slider("slides_count", "Number of Slides", min = 5f, max = 10f, defaultValue = 7f)
            )
        ),
        ToolConfig(
            id = "ig_story_idea",
            name = "Story Idea Generator",
            platform = Platform.INSTAGRAM,
            category = "Content",
            description = "Interactive 24-hour Stories with poll stickers, quiz prompts, and DM triggers.",
            iconKey = "story",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("story", "poll", "sticker", "interactive"),
            fields = listOf(
                InputField.Text("niche", "Account Focus", "e.g., Coffee Roasting & Cafe Lifestyle"),
                InputField.Select("goal", "Daily Story Goal", listOf("Boost DM Conversations", "Poll Audience for Feedback", "Warm Up for Product Launch", "Casual Day in the Life"))
            )
        ),
        ToolConfig(
            id = "ig_post_idea",
            name = "Instagram Post Idea Generator",
            platform = Platform.INSTAGRAM,
            category = "Content",
            description = "Fresh ideas for static graphics, infographics, tweet quotes, and photo dumps.",
            iconKey = "lightbulb",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("post ideas", "graphics", "photo dump", "quotes"),
            fields = listOf(
                InputField.Text("niche", "Niche", "e.g., Mindset & Meditation"),
                InputField.Select("format", "Preferred Post Format", listOf("Tweet / Quote Graphic", "Infographic / Diagram", "Photo with Long-form Caption", "Meme / Relatable Culture"))
            )
        ),
        ToolConfig(
            id = "ig_content_calendar",
            name = "Instagram Content Calendar",
            platform = Platform.INSTAGRAM,
            category = "Content",
            description = "Weekly grid schedule balancing Reels, Carousels, Stories, and Broadcast channel drops.",
            iconKey = "calendar",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("calendar", "schedule", "grid", "weekly plan"),
            fields = listOf(
                InputField.Text("niche", "Your Niche", "e.g., Skincare & Beauty Routine"),
                InputField.Select("posting_pace", "Posting Frequency", listOf("3 Reels + 2 Carousels / week", "Daily Reels + Daily Stories", "4 Posts / week (Balanced)"))
            )
        ),

        // === Instagram Growth (11-20) ===
        ToolConfig(
            id = "ig_hashtag",
            name = "Instagram Hashtag Generator",
            platform = Platform.INSTAGRAM,
            category = "Growth",
            description = "Targeted hashtags segmented by Low (<50k), Mid (50k-500k), and Mega (500k+) competition.",
            iconKey = "hashtag",
            isPro = false,
            outputType = ToolOutputType.TAGS,
            keywords = listOf("hashtag", "reach", "explore page", "tags"),
            fields = listOf(
                InputField.Text("topic", "Post Topic", "e.g., Healthy High Protein Meal Prep"),
                InputField.Text("niche", "Niche", "e.g., Fitness Nutrition"),
                InputField.Slider("count", "Hashtag Total", min = 10f, max = 30f, defaultValue = 20f)
            )
        ),
        ToolConfig(
            id = "ig_keyword",
            name = "Instagram Keyword Generator",
            platform = Platform.INSTAGRAM,
            category = "Growth",
            description = "Keywords to embed in caption text, alt text, and bio for Instagram search algorithm.",
            iconKey = "keyword",
            isPro = false,
            outputType = ToolOutputType.TAGS,
            keywords = listOf("keyword", "search", "seo", "alt text"),
            fields = listOf(
                InputField.Text("topic", "Core Topic or Product", "e.g., Handmade Ceramic Mugs"),
                InputField.Select("target_buyer", "Customer Type", listOf("Eco-conscious Shoppers", "Home Decor Enthusiasts", "Gift Buyers"))
            )
        ),
        ToolConfig(
            id = "ig_seo_caption",
            name = "Instagram SEO Caption Generator",
            platform = Platform.INSTAGRAM,
            category = "Growth",
            description = "Naturally integrate high-ranking search terms into your caption body to rank on Explore.",
            iconKey = "seo",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("seo caption", "explore page", "search intent", "ranking"),
            fields = listOf(
                InputField.Text("target_query", "Target Search Term", "e.g., Best core exercises for lower back pain"),
                InputField.TextArea("content_points", "Tips or Solution Steps", "1. Bird dogs 2. Dead bugs 3. Side planks")
            )
        ),
        ToolConfig(
            id = "ig_engagement_caption",
            name = "Engagement Caption Generator",
            platform = Platform.INSTAGRAM,
            category = "Growth",
            description = "Captions built specifically to trigger comments, shares to Story, and DM automations.",
            iconKey = "comment",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("engagement", "comment", "manychat", "share"),
            fields = listOf(
                InputField.Text("topic", "Post Subject", "e.g., Free checklist for buying your first home"),
                InputField.Text("keyword_trigger", "Comment Trigger Word (for ManyChat)", "e.g., GUIDE or CHECKLIST")
            )
        ),
        ToolConfig(
            id = "ig_cta_generator",
            name = "CTA Generator",
            platform = Platform.INSTAGRAM,
            category = "Growth",
            description = "High-converting closing lines for captions, bio buttons, and Reel end screens.",
            iconKey = "bell",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("cta", "call to action", "dm", "link in bio"),
            fields = listOf(
                InputField.Select("cta_goal", "Primary Action", listOf("Comment a Word for Link", "Share to Your Story", "Save for Later", "Click Link in Bio", "Follow for Part 2")),
                InputField.Slider("count", "Number of Variations", min = 3f, max = 8f, defaultValue = 5f)
            )
        ),
        ToolConfig(
            id = "ig_engagement_question",
            name = "Engagement Question Generator",
            platform = Platform.INSTAGRAM,
            category = "Growth",
            description = "Irresistible questions to end your captions with that viewers can't resist answering.",
            iconKey = "question",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("questions", "debate", "comments", "community"),
            fields = listOf(
                InputField.Text("topic", "Post Topic", "e.g., Remote work vs Office work in 2026"),
                InputField.Select("type", "Question Angle", listOf("A vs B Preference", "Would You Rather", "Share Your Biggest Struggle", "Controversial Opinion Poll"))
            )
        ),
        ToolConfig(
            id = "ig_comment_reply",
            name = "Comment Reply Generator",
            platform = Platform.INSTAGRAM,
            category = "Growth",
            description = "Witty, thoughtful, and community-building replies to keep post momentum alive.",
            iconKey = "chat",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("replies", "comments", "speed reply", "community"),
            fields = listOf(
                InputField.Text("user_comment", "Comment Received", "e.g., Wow I didn't know this! Does this work on Android?"),
                InputField.Select("tone", "Reply Vibe", listOf("Helpful & Friendly", "Witty & Fun", "Inviting to DM for Details", "Gratitude & High Energy"))
            )
        ),
        ToolConfig(
            id = "ig_collab_pitch",
            name = "Collaboration Pitch Generator",
            platform = Platform.INSTAGRAM,
            category = "Growth",
            description = "Warm outreach DM to pitch joint Live sessions, co-authored Reels, and giveaways.",
            iconKey = "users",
            isPro = true,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("collaboration", "dm pitch", "co-author", "joint live"),
            fields = listOf(
                InputField.Text("creator_handle", "Target Creator / Handle", "e.g., @designwithsam"),
                InputField.Text("collab_concept", "Your Idea for Joint Content", "e.g., 30-min Live reviewing portfolio websites"),
                InputField.Text("mutual_value", "Why it benefits their audience", "e.g., Cross-pollinating 20k design enthusiasts")
            )
        ),
        ToolConfig(
            id = "ig_influencer_outreach",
            name = "Influencer Outreach Message Generator",
            platform = Platform.INSTAGRAM,
            category = "Growth",
            description = "Professional influencer DM/email to propose gifting, PR seeding, or paid promos.",
            iconKey = "mail",
            isPro = true,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("influencer", "outreach", "pr package", "gifting"),
            fields = listOf(
                InputField.Text("brand_name", "Your Brand Name", "e.g., Lumina Coffee Co."),
                InputField.Text("product", "Product Offered", "e.g., Special Reserve Espresso Bean Gift Box"),
                InputField.Select("deal_type", "Offer Type", listOf("Free Gift (No Strings Attached)", "Paid Sponsored Reel", "Affiliate Commission (20%)"))
            )
        ),
        ToolConfig(
            id = "ig_brand_deal_pitch",
            name = "Brand Deal Pitch Generator",
            platform = Platform.INSTAGRAM,
            category = "Growth",
            description = "Direct pitch message for creators to land sponsored Instagram campaigns.",
            iconKey = "briefcase",
            isPro = true,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("brand deal", "sponsorship", "media kit", "rates"),
            fields = listOf(
                InputField.Text("brand_name", "Target Brand", "e.g., Notion / Gymshark"),
                InputField.Text("niche_and_stats", "Your Audience Stats", "e.g., 45k followers, 8.2% engagement in Productivity"),
                InputField.Text("campaign_idea", "Creative Concept", "e.g., 1 Dedicated Reel + 3 Story frames showing daily workflow")
            )
        ),

        // === Instagram Strategy (21-30) ===
        ToolConfig(
            id = "ig_niche_finder",
            name = "Instagram Niche Finder",
            platform = Platform.INSTAGRAM,
            category = "Strategy",
            description = "Identify profitable visual niches and aesthetics with high affiliate conversion.",
            iconKey = "compass",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("niche", "aesthetic", "monetization", "sub-niche"),
            fields = listOf(
                InputField.Text("interests", "Your Skills & Interests", "e.g., Plant care, Interior design, Photography"),
                InputField.Select("business_model", "Target Monetization", listOf("Digital Products & Courses", "Brand Partnerships", "Coaching / Services", "Physical E-commerce"))
            )
        ),
        ToolConfig(
            id = "ig_content_pillars",
            name = "Content Pillar Generator",
            platform = Platform.INSTAGRAM,
            category = "Strategy",
            description = "Define your 4 core content buckets (Educate, Entertain, Inspire, Promote).",
            iconKey = "cluster",
            isPro = false,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("pillars", "content buckets", "strategy", "consistency"),
            fields = listOf(
                InputField.Text("brand_theme", "Core Account Theme", "e.g., Financial Literacy for Women"),
                InputField.Text("audience", "Target Follower", "e.g., Female professionals in their 20s and 30s")
            )
        ),
        ToolConfig(
            id = "ig_audience_persona",
            name = "Audience Persona Generator",
            platform = Platform.INSTAGRAM,
            category = "Strategy",
            description = "Map out your follower's pain points, aspirational lifestyle, and buying triggers.",
            iconKey = "users",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("persona", "avatar", "psychographics", "follower"),
            fields = listOf(
                InputField.Text("niche", "Instagram Page Theme", "e.g., Solo Female Travel"),
                InputField.Select("target_age", "Age Demographic", listOf("18-24 (Gen Z)", "25-34 (Millennials)", "35-50 (Gen X)"))
            )
        ),
        ToolConfig(
            id = "ig_growth_strategy",
            name = "Instagram Growth Strategy Generator",
            platform = Platform.INSTAGRAM,
            category = "Strategy",
            description = "Step-by-step 0 to 10k follower growth blueprint with daily non-negotiable actions.",
            iconKey = "strategy",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("growth strategy", "0 to 10k", "algorithm", "daily actions"),
            fields = listOf(
                InputField.Text("current_followers", "Current Follower Count", "e.g., 250 followers"),
                InputField.Text("niche", "Niche", "e.g., AI Tools & Automations"),
                InputField.Select("time_commitment", "Daily Hours Available", listOf("1 Hour / day", "2-3 Hours / day", "Full-time Creator"))
            )
        ),
        ToolConfig(
            id = "ig_reels_strategy",
            name = "Reels Content Strategy Generator",
            platform = Platform.INSTAGRAM,
            category = "Strategy",
            description = "Comprehensive Reels production workflow to feed the algorithm 5x weekly.",
            iconKey = "flame",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("reels strategy", "batching", "trending sounds", "viral"),
            fields = listOf(
                InputField.Text("niche", "Content Niche", "e.g., Personal Styling & Capsule Wardrobe"),
                InputField.Select("equipment", "Filming Equipment", listOf("Smartphone only", "Ring light + Phone", "Mirrorless Camera Studio"))
            )
        ),
        ToolConfig(
            id = "ig_viral_reels_idea",
            name = "Viral Reels Idea Generator",
            platform = Platform.INSTAGRAM,
            category = "Strategy",
            description = "High-shareability Reel ideas engineered around relatable life observations.",
            iconKey = "trending",
            isPro = true,
            outputType = ToolOutputType.LIST,
            keywords = listOf("viral reels", "shares", "relatable", "trending"),
            fields = listOf(
                InputField.Text("niche", "Your Niche", "e.g., Remote Software Engineering"),
                InputField.Select("viral_concept", "Concept Frame", listOf("Nobody is talking about this", "Day in life (Reality vs Expectation)", "Mistake that cost me thousands", "Things that just make sense"))
            )
        ),
        ToolConfig(
            id = "ig_trending_content",
            name = "Trending Content Idea Generator",
            platform = Platform.INSTAGRAM,
            category = "Strategy",
            description = "Adapt current internet memes and pop culture templates to your specific niche.",
            iconKey = "sparkle",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("memes", "pop culture", "trends", "relatable"),
            fields = listOf(
                InputField.Text("niche", "Your Industry / Niche", "e.g., Real Estate Agents"),
                InputField.Select("vibe", "Humor Vibe", listOf("Self-deprecating & Funny", "Sarcastic & Honest", "Uplifting & Motivating"))
            )
        ),
        ToolConfig(
            id = "ig_username_generator",
            name = "Instagram Username Generator",
            platform = Platform.INSTAGRAM,
            category = "Strategy",
            description = "Clean, memorable, aesthetic handles with minimal underscores and numbers.",
            iconKey = "at",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("username", "handle", "aesthetic", "name"),
            fields = listOf(
                InputField.Text("name_or_keyword", "Base Name / Keyword", "e.g., Luna / Studio / Glow"),
                InputField.Select("style", "Aesthetic Style", listOf("Minimal & Clean (e.g., @the.luna.studio)", "Creative & Artsy (e.g., @lunalab)", "Personal Brand (e.g., @iamluna)", "Professional (e.g., @luna.co)"))
            )
        ),
        ToolConfig(
            id = "ig_page_name",
            name = "Instagram Page Name Generator",
            platform = Platform.INSTAGRAM,
            category = "Strategy",
            description = "Optimized Name Field (bold text under handle) for Instagram search discoverability.",
            iconKey = "brand",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("page name", "searchable name", "seo name", "bold field"),
            fields = listOf(
                InputField.Text("your_name", "Your First Name", "e.g., Maya"),
                InputField.Text("profession", "Profession / Main Keyword", "e.g., UGC Creator & Video Editor"),
                InputField.Text("location", "City / Country (optional)", "e.g., London / UK")
            )
        ),
        ToolConfig(
            id = "ig_content_repurposer",
            name = "Instagram Content Repurposer",
            platform = Platform.INSTAGRAM,
            category = "Strategy",
            description = "Convert any long article or video transcript into 1 Reel, 1 Carousel, and 3 Stories.",
            iconKey = "repurpose",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("repurpose", "carousel", "story", "multiplier"),
            fields = listOf(
                InputField.Text("topic", "Source Content Title", "e.g., The 80/20 Rule for Personal Productivity"),
                InputField.TextArea("content_text", "Core Ideas / Text", "List main points or paste paragraph...")
            )
        )
    )
}
