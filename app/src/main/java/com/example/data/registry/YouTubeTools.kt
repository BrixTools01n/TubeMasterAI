package com.example.data.registry

import com.example.model.InputField
import com.example.model.Platform
import com.example.model.ToolConfig
import com.example.model.ToolOutputType

object YouTubeTools {
    val list: List<ToolConfig> = listOf(
        // === YouTube SEO & Discovery (1-10) ===
        ToolConfig(
            id = "yt_viral_title",
            name = "Viral Title Generator",
            platform = Platform.YOUTUBE,
            category = "SEO & Discovery",
            description = "Create irresistible high-CTR video titles that spark curiosity and drive clicks.",
            iconKey = "title",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("title", "viral", "ctr", "clickbait", "youtube"),
            fields = listOf(
                InputField.Text("topic", "Video Topic / Summary", "e.g., iPhone 16 Pro Max Camera Test vs DSLR", isRequired = true),
                InputField.Text("niche", "Niche / Category", "e.g., Tech Reviews, Fitness, Gaming", defaultValue = "Tech"),
                InputField.Select("audience", "Target Audience", listOf("General Public", "Beginners", "Experts / Pros", "Students", "Entrepreneurs")),
                InputField.Select("tone", "Title Style", listOf("Curiosity & Mystery", "Urgent & High Impact", "How-To / Educational", "Extreme & Dramatic", "Controversial")),
                InputField.Slider("count", "Number of Titles", min = 3f, max = 10f, defaultValue = 5f, unit = "titles")
            )
        ),
        ToolConfig(
            id = "yt_seo_title",
            name = "SEO Title Generator",
            platform = Platform.YOUTUBE,
            category = "SEO & Discovery",
            description = "Generate search-optimized video titles with high-volume search keywords.",
            iconKey = "search",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("seo", "rank", "search", "keyword", "title"),
            fields = listOf(
                InputField.Text("topic", "Primary Keyword / Topic", "e.g., Learn Python for Beginners 2026"),
                InputField.Text("target_keyword", "Secondary Target Keyword", "e.g., Python tutorial step by step"),
                InputField.Select("intent", "Search Intent", listOf("Tutorial / How-to", "Product Review", "Listicle / Top 10", "Comparison", "Guide")),
                InputField.Slider("count", "Titles to Generate", min = 3f, max = 10f, defaultValue = 5f)
            )
        ),
        ToolConfig(
            id = "yt_description",
            name = "YouTube Description Generator",
            platform = Platform.YOUTUBE,
            category = "SEO & Discovery",
            description = "Write perfect SEO-optimized descriptions with timestamps, links, and keywords.",
            iconKey = "description",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("description", "seo", "timestamps", "links"),
            fields = listOf(
                InputField.Text("title", "Video Title", "e.g., 10 Productivity Hacks That Saved My Life"),
                InputField.TextArea("summary", "What happens in the video?", "Brief breakdown of key talking points"),
                InputField.Text("keywords", "Target Keywords (comma separated)", "productivity, time management, habits"),
                InputField.Toggle("include_timestamps", "Include Placeholder Timestamps", defaultValue = true),
                InputField.Toggle("include_socials", "Include Social & Affiliate Link Slots", defaultValue = true)
            )
        ),
        ToolConfig(
            id = "yt_tags",
            name = "YouTube Tags Generator",
            platform = Platform.YOUTUBE,
            category = "SEO & Discovery",
            description = "Generate 20+ perfectly optimized video tags for high search discovery.",
            iconKey = "tags",
            isPro = false,
            outputType = ToolOutputType.TAGS,
            keywords = listOf("tags", "rank", "seo", "algorithm"),
            fields = listOf(
                InputField.Text("topic", "Video Title or Topic", "e.g., Best Budget Gaming Setup 2026"),
                InputField.Text("niche", "Channel Niche", "e.g., PC Gaming & Hardware"),
                InputField.Select("tag_type", "Tag Variety", listOf("Broad + Specific Mix", "Long-Tail Heavy", "Competitor Focused"))
            )
        ),
        ToolConfig(
            id = "yt_hashtag",
            name = "YouTube Hashtag Generator",
            platform = Platform.YOUTUBE,
            category = "SEO & Discovery",
            description = "Best trending hashtags segmented by broad, niche, and viral tiers.",
            iconKey = "hashtag",
            isPro = false,
            outputType = ToolOutputType.TAGS,
            keywords = listOf("hashtag", "shorts", "trending", "discovery"),
            fields = listOf(
                InputField.Text("topic", "Video Content Topic", "e.g., Morning Routine 5 AM Club"),
                InputField.Select("format", "Video Format", listOf("YouTube Shorts", "Long-form Video", "Live Stream")),
                InputField.Slider("count", "Number of Hashtags", min = 5f, max = 20f, defaultValue = 10f)
            )
        ),
        ToolConfig(
            id = "yt_keyword",
            name = "YouTube Keyword Generator",
            platform = Platform.YOUTUBE,
            category = "SEO & Discovery",
            description = "Find high-volume, low-competition keywords for the YouTube search bar.",
            iconKey = "keyword",
            isPro = true,
            outputType = ToolOutputType.TAGS,
            keywords = listOf("keyword", "search", "volume", "competition"),
            fields = listOf(
                InputField.Text("seed_keyword", "Seed Keyword", "e.g., Weight Loss Diet"),
                InputField.Select("audience_level", "Searcher Intent Level", listOf("Complete Beginner", "Intermediate", "Advanced")),
                InputField.Select("region", "Target Region", listOf("Global (English)", "United States", "India", "UK", "Canada"))
            )
        ),
        ToolConfig(
            id = "yt_keyword_cluster",
            name = "Keyword Cluster Generator",
            platform = Platform.YOUTUBE,
            category = "SEO & Discovery",
            description = "Group related search terms into thematic clusters for authority playlists.",
            iconKey = "cluster",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("cluster", "playlist", "authority", "seo"),
            fields = listOf(
                InputField.Text("main_topic", "Core Pillar Topic", "e.g., Personal Finance & Investing"),
                InputField.Slider("cluster_count", "Number of Sub-Clusters", min = 3f, max = 6f, defaultValue = 4f)
            )
        ),
        ToolConfig(
            id = "yt_search_intent",
            name = "Search Intent Analyzer",
            platform = Platform.YOUTUBE,
            category = "SEO & Discovery",
            description = "Analyze user intent for any query to tailor video pacing and retention.",
            iconKey = "intent",
            isPro = false,
            outputType = ToolOutputType.KEY_VALUE,
            keywords = listOf("intent", "retention", "audience", "search"),
            fields = listOf(
                InputField.Text("query", "Target Search Query", "e.g., How to fix blurry 4k camera"),
                InputField.Select("video_length", "Planned Video Length", listOf("Under 60s (Shorts)", "3-7 Minutes", "8-15 Minutes", "20+ Minutes"))
            )
        ),
        ToolConfig(
            id = "yt_seo_optimizer",
            name = "Video SEO Optimizer",
            platform = Platform.YOUTUBE,
            category = "SEO & Discovery",
            description = "Full metadata audit & optimization package (Title, Desc, Tags, Chapters).",
            iconKey = "tune",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("audit", "seo", "optimize", "metadata"),
            fields = listOf(
                InputField.Text("draft_title", "Draft Video Title", "e.g., My home studio setup"),
                InputField.TextArea("draft_desc", "Current Description or Talking Points", "I show the lights, mic, camera..."),
                InputField.Text("main_keyword", "Main Keyword Target", "e.g., budget youtube studio setup")
            )
        ),
        ToolConfig(
            id = "yt_seo_score",
            name = "YouTube SEO Score Analyzer",
            platform = Platform.YOUTUBE,
            category = "SEO & Discovery",
            description = "Evaluate your title and description SEO strength with score & fix suggestions.",
            iconKey = "analytics",
            isPro = false,
            outputType = ToolOutputType.KEY_VALUE,
            keywords = listOf("score", "analyzer", "rate", "grade"),
            fields = listOf(
                InputField.Text("title", "Video Title to Grade", "e.g., How I built an app in 24 hours"),
                InputField.TextArea("description", "Video Description", "In this video I code..."),
                InputField.Text("tags", "Video Tags (optional)", "coding, react native, ai")
            )
        ),

        // === YouTube Content (11-20) ===
        ToolConfig(
            id = "yt_video_idea",
            name = "Video Idea Generator",
            platform = Platform.YOUTUBE,
            category = "Content",
            description = "Generate 10 fresh, highly clickable video concepts tailored to your niche.",
            iconKey = "idea",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("ideas", "brainstorm", "concepts", "content"),
            fields = listOf(
                InputField.Text("niche", "Channel Niche / Topic", "e.g., Filmmaking on a budget"),
                InputField.Select("format", "Idea Style", listOf("Challenges & Experiments", "How-To Guides", "Documentary / Deep Dive", "Gear & Product Reviews", "Top 10 / Listicles")),
                InputField.Slider("count", "Number of Ideas", min = 5f, max = 12f, defaultValue = 8f)
            )
        ),
        ToolConfig(
            id = "yt_viral_idea",
            name = "Viral Video Idea Generator",
            platform = Platform.YOUTUBE,
            category = "Content",
            description = "Brainstorm high-stakes, emotion-driven video concepts with viral potential.",
            iconKey = "flame",
            isPro = true,
            outputType = ToolOutputType.LIST,
            keywords = listOf("viral", "trending", "high views", "hook"),
            fields = listOf(
                InputField.Text("niche", "Channel Niche", "e.g., Street Food, Fitness, Travel"),
                InputField.Select("viral_trigger", "Psychological Angle", listOf("Extreme Challenge ($0 vs $1000)", "Breaking a Myth / Secret", "Emotional Transformation", "First Time Doing X", "Celebrity / Pop Culture")),
                InputField.Slider("count", "Ideas Count", min = 3f, max = 8f, defaultValue = 5f)
            )
        ),
        ToolConfig(
            id = "yt_script_generator",
            name = "YouTube Script Generator",
            platform = Platform.YOUTUBE,
            category = "Content",
            description = "Full production script with Hook, Intro, Main Content, Visual B-Roll cues, and CTA.",
            iconKey = "script",
            isPro = true,
            outputType = ToolOutputType.SCRIPT,
            keywords = listOf("script", "writer", "b-roll", "teleprompter"),
            fields = listOf(
                InputField.Text("topic", "Video Title / Topic", "e.g., 5 AI Tools That Will Replace Programmers"),
                InputField.Select("duration", "Estimated Duration", listOf("3-5 Minutes", "8-10 Minutes", "15+ Minutes")),
                InputField.Select("tone", "Speaker Tone", listOf("Energetic & Entertaining", "Authoritative & Educational", "Casual & Conversational", "Cinematic Storytelling")),
                InputField.Toggle("include_broll", "Include Visual / B-Roll Director Cues", defaultValue = true)
            )
        ),
        ToolConfig(
            id = "yt_short_script",
            name = "Short Video Script Generator",
            platform = Platform.YOUTUBE,
            category = "Content",
            description = "Fast-paced, 30-60 second YouTube Shorts script designed for 100% viewer retention.",
            iconKey = "shorts",
            isPro = false,
            outputType = ToolOutputType.SCRIPT,
            keywords = listOf("shorts", "60s", "tiktok", "reels", "script"),
            fields = listOf(
                InputField.Text("topic", "Shorts Topic", "e.g., The secret psychology of McDonald's logo"),
                InputField.Select("duration", "Length", listOf("15 Seconds (Ultra fast)", "30 Seconds (Standard)", "60 Seconds (Full)")),
                InputField.Select("hook_style", "Opening Hook", listOf("Pattern Interrupt", "Shocking Question", "Bold Counter-Intuitive Claim", "Relatable Frustration"))
            )
        ),
        ToolConfig(
            id = "yt_long_form_script",
            name = "Long-Form Script Generator",
            platform = Platform.YOUTUBE,
            category = "Content",
            description = "Detailed, episodic long-form video script with deep dives and chapter structures.",
            iconKey = "document",
            isPro = true,
            outputType = ToolOutputType.SCRIPT,
            keywords = listOf("documentary", "essay", "long form", "script"),
            fields = listOf(
                InputField.Text("topic", "Documentary or Video Essay Topic", "e.g., The Rise and Fall of BlackBerry"),
                InputField.TextArea("key_points", "Key Arguments or Research Facts", "List main chapters or chronological milestones"),
                InputField.Select("tone", "Narrative Tone", listOf("Investigative & Dramatic", "Inspiring & Philosophical", "Deep Academic Analysis"))
            )
        ),
        ToolConfig(
            id = "yt_hook_generator",
            name = "YouTube Hook Generator",
            platform = Platform.YOUTUBE,
            category = "Content",
            description = "First 5-10 second opening hooks to prevent viewers from clicking away.",
            iconKey = "hook",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("hook", "retention", "intro", "first 5 seconds"),
            fields = listOf(
                InputField.Text("video_topic", "Video Title / Premise", "e.g., I tried surviving on $1 in Tokyo"),
                InputField.Select("hook_type", "Hook Angle", listOf("Visual Shock + Stakes", "Open Loop / Mystery", "Pain Point / Agitation", "Big Promise & Proof", "Behind The Scenes Cliffhanger")),
                InputField.Slider("count", "Hook Variations", min = 3f, max = 8f, defaultValue = 5f)
            )
        ),
        ToolConfig(
            id = "yt_video_intro",
            name = "Video Intro Generator",
            platform = Platform.YOUTUBE,
            category = "Content",
            description = "Seamless 15-30s video introductions that set up stakes and deliver on title promises.",
            iconKey = "intro",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("intro", "opening", "stakes", "presentation"),
            fields = listOf(
                InputField.Text("title", "Video Title", "e.g., 7 Ways to Double Your Productivity"),
                InputField.Text("speaker", "Presenter Name / Persona", "e.g., Tech Creator Alex"),
                InputField.Select("style", "Delivery Style", listOf("Fast & Punchy", "Story-driven", "Direct to the Point"))
            )
        ),
        ToolConfig(
            id = "yt_video_outro",
            name = "Video Outro Generator",
            platform = Platform.YOUTUBE,
            category = "Content",
            description = "High-converting outro scripts that seamlessly bridge viewers into the next video.",
            iconKey = "outro",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("outro", "endscreen", "bridge", "session time"),
            fields = listOf(
                InputField.Text("next_video", "Next Recommended Video Topic", "e.g., How to color grade in Premiere Pro"),
                InputField.Select("cta_goal", "Primary Goal", listOf("Click Next Video (Session Time)", "Subscribe to Channel", "Download Free Template", "Join Membership"))
            )
        ),
        ToolConfig(
            id = "yt_cta_generator",
            name = "Call-To-Action Generator",
            platform = Platform.YOUTUBE,
            category = "Content",
            description = "Organic subscribe, like, and comment call-to-actions that don't sound spammy.",
            iconKey = "bell",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("cta", "subscribe", "like", "notification"),
            fields = listOf(
                InputField.Text("niche", "Channel Theme", "e.g., Personal Finance & Stocks"),
                InputField.Select("placement", "CTA Placement", listOf("Mid-Roll Organic Drop", "Post-Climax Hook", "Pre-Outro Wrap Up", "Pinned Comment Prompt")),
                InputField.Slider("count", "CTA Options", min = 3f, max = 6f, defaultValue = 4f)
            )
        ),
        ToolConfig(
            id = "yt_storytelling_script",
            name = "Storytelling Script Generator",
            platform = Platform.YOUTUBE,
            category = "Content",
            description = "Apply the Hero's Journey framework to transform any topic into an emotional story.",
            iconKey = "story",
            isPro = true,
            outputType = ToolOutputType.SCRIPT,
            keywords = listOf("story", "heros journey", "emotion", "narrative"),
            fields = listOf(
                InputField.Text("protagonist_goal", "Core Goal or Challenge", "e.g., Leaving my 9-to-5 job with $500 in bank"),
                InputField.Text("obstacle", "Major Conflict or Failure", "e.g., 3 months without any clients and rent due"),
                InputField.Text("breakthrough", "The Epiphany / Solution", "e.g., Finding the right client acquisition framework")
            )
        ),

        // === YouTube Growth (21-30) ===
        ToolConfig(
            id = "yt_channel_name",
            name = "Channel Name Generator",
            platform = Platform.YOUTUBE,
            category = "Growth",
            description = "Catchy, brandable, and memorable channel names categorized by aesthetic.",
            iconKey = "brand",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("channel name", "branding", "handle", "username"),
            fields = listOf(
                InputField.Text("topic", "Niche / Topics You Cover", "e.g., Cooking, Tech, AI, Gaming"),
                InputField.Text("keywords", "Specific Words to Include (optional)", "e.g., Lab, Studio, Byte"),
                InputField.Select("style", "Naming Vibe", listOf("Modern & Minimal", "Punchy & One-Word", "Clever & Witty", "Professional & Media House", "Personal Brand"))
            )
        ),
        ToolConfig(
            id = "yt_channel_description",
            name = "Channel Description Generator",
            platform = Platform.YOUTUBE,
            category = "Growth",
            description = "Channel About page bio with mission statement, upload schedule, and business email CTA.",
            iconKey = "about",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("about", "bio", "channel", "growth"),
            fields = listOf(
                InputField.Text("channel_name", "Channel Name", "e.g., CodeCraft"),
                InputField.Text("niche", "Channel Mission / What Viewers Learn", "e.g., Helping aspiring developers land software engineering jobs"),
                InputField.Text("upload_days", "Upload Frequency", "e.g., Every Tuesday and Friday at 5 PM EST")
            )
        ),
        ToolConfig(
            id = "yt_bio_generator",
            name = "YouTube Bio Generator",
            platform = Platform.YOUTUBE,
            category = "Growth",
            description = "Short 1-2 sentence channel header tagline that explains what value you deliver.",
            iconKey = "user",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("bio", "tagline", "header", "value proposition"),
            fields = listOf(
                InputField.Text("niche", "Your Niche", "e.g., Minimalist Home Design"),
                InputField.Text("target_viewer", "Who do you help?", "e.g., Small apartment dwellers"),
                InputField.Select("tone", "Tagline Tone", listOf("Inspiring", "Bold & Direct", "Clever / Humorous", "Academic & Trustworthy"))
            )
        ),
        ToolConfig(
            id = "yt_series_ideas",
            name = "Video Series Idea Generator",
            platform = Platform.YOUTUBE,
            category = "Growth",
            description = "Binge-worthy recurring series concepts to turn casual viewers into loyal subscribers.",
            iconKey = "series",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("series", "binge", "playlist", "retention"),
            fields = listOf(
                InputField.Text("niche", "Channel Niche", "e.g., Retro Tech Restoration"),
                InputField.Select("series_type", "Series Format", listOf("Chronological Challenge (Day 1 to 30)", "Episode Interviews / Breakdowns", "Before vs After Transformations", "Fixing Viewer Submissions"))
            )
        ),
        ToolConfig(
            id = "yt_content_calendar",
            name = "Content Calendar Generator",
            platform = Platform.YOUTUBE,
            category = "Growth",
            description = "30-day scheduled roadmap of videos balancing search intent, trending topics, and Shorts.",
            iconKey = "calendar",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("calendar", "schedule", "plan", "monthly"),
            fields = listOf(
                InputField.Text("niche", "Channel Niche", "e.g., Fitness & Meal Prep"),
                InputField.Select("frequency", "Uploads Per Week", listOf("1 Long-form + 3 Shorts", "2 Long-form + 5 Shorts", "3 Long-form Videos", "Daily Shorts Only")),
                InputField.Select("primary_goal", "Monthly Focus", listOf("Subscriber Growth", "Search Traffic Authority", "Sponsorship & Monetization", "Community Engagement"))
            )
        ),
        ToolConfig(
            id = "yt_upload_schedule",
            name = "Upload Schedule Generator",
            platform = Platform.YOUTUBE,
            category = "Growth",
            description = "Determine best publish days and time slots based on audience geography and niche.",
            iconKey = "clock",
            isPro = false,
            outputType = ToolOutputType.KEY_VALUE,
            keywords = listOf("timing", "schedule", "peak hours", "algorithm"),
            fields = listOf(
                InputField.Select("target_region", "Primary Audience Geography", listOf("United States & Canada", "India & South Asia", "Europe / UK", "Global Mix")),
                InputField.Select("niche_type", "Content Category", listOf("Gaming / Entertainment", "Finance / Business", "Education / Tech", "Lifestyle / Vlogs"))
            )
        ),
        ToolConfig(
            id = "yt_audience_persona",
            name = "Audience Persona Generator",
            platform = Platform.YOUTUBE,
            category = "Growth",
            description = "Detailed demographic & psychographic profile of your ideal YouTube viewer.",
            iconKey = "users",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("persona", "demographics", "avatar", "psychology"),
            fields = listOf(
                InputField.Text("channel_topic", "Channel Topic", "e.g., Solopreneurship & SaaS Building"),
                InputField.Select("age_group", "Target Age Tier", listOf("Gen Z (16-24)", "Young Adults (25-34)", "Established (35-49)", "All Ages"))
            )
        ),
        ToolConfig(
            id = "yt_niche_finder",
            name = "Niche Finder",
            platform = Platform.YOUTUBE,
            category = "Growth",
            description = "Discover untapped sub-niches with high CPM potential and low creator saturation.",
            iconKey = "compass",
            isPro = true,
            outputType = ToolOutputType.LIST,
            keywords = listOf("niche", "cpm", "untapped", "monetization"),
            fields = listOf(
                InputField.Text("broad_interest", "Your General Passions / Skills", "e.g., Coding, Photography, Woodworking"),
                InputField.Select("monetization_focus", "Preferred Revenue Stream", listOf("High YouTube AdSense CPM", "Affiliate Marketing", "Digital Courses & Products", "Brand Sponsorships"))
            )
        ),
        ToolConfig(
            id = "yt_competitor_ideas",
            name = "Competitor Content Idea Generator",
            platform = Platform.YOUTUBE,
            category = "Growth",
            description = "Reverse engineer top competitor topics with unique value-add angles.",
            iconKey = "target",
            isPro = true,
            outputType = ToolOutputType.LIST,
            keywords = listOf("competitor", "differentiation", "angles", "growth"),
            fields = listOf(
                InputField.Text("competitor_video", "Competitor Video Title That Performed Well", "e.g., Why Most People Fail at Dropshipping"),
                InputField.Text("your_angle", "Your Channel Stance / Advantage", "e.g., Transparent real-data experiments without fluff")
            )
        ),
        ToolConfig(
            id = "yt_trending_topics",
            name = "Trending Topic Idea Generator",
            platform = Platform.YOUTUBE,
            category = "Growth",
            description = "Capitalize on current news, platform trends, and cultural moments in your niche.",
            iconKey = "trending",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("trending", "news", "viral", "hacks"),
            fields = listOf(
                InputField.Text("niche", "Your Niche", "e.g., Artificial Intelligence"),
                InputField.Select("news_angle", "Angle Style", listOf("Breaking Reaction / Breakdown", "How It Affects You (Viewer)", "Predictions for Next Year", "Testing it Live"))
            )
        ),

        // === YouTube Optimization (31-40) ===
        ToolConfig(
            id = "yt_thumbnail_text",
            name = "Thumbnail Text Generator",
            platform = Platform.YOUTUBE,
            category = "Optimization",
            description = "Punchy 2-4 word thumbnail text concepts that complement (not repeat) the video title.",
            iconKey = "photo",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("thumbnail", "ctr", "image", "visual text"),
            fields = listOf(
                InputField.Text("video_title", "Video Title", "e.g., 5 Coding Habits That Waste 10 Hours a Week"),
                InputField.Select("emotion", "Visual Emotion", listOf("Shock / Disbelief", "Curiosity / Mystery", "Satisfaction / Win", "Urgency / Stop!")),
                InputField.Slider("count", "Options", min = 4f, max = 10f, defaultValue = 6f)
            )
        ),
        ToolConfig(
            id = "yt_thumbnail_ideas",
            name = "Thumbnail Idea Generator",
            platform = Platform.YOUTUBE,
            category = "Optimization",
            description = "Visual layout prompts, color contrast advice, face expressions, and graphic composition.",
            iconKey = "palette",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("thumbnail", "design", "composition", "graphics"),
            fields = listOf(
                InputField.Text("title", "Video Title", "e.g., I Built a Secret Underground Bunker"),
                InputField.Select("creator_style", "Creator Thumbnail Style", listOf("MrBeast Style (High Saturation & Action)", "Veritasium Style (Minimal & Mysterious)", "Ali Abdaal Style (Clean & Aesthetic)", "Gaming Style (Dynamic Glow & Reaction)"))
            )
        ),
        ToolConfig(
            id = "yt_ab_titles",
            name = "A/B Title Generator",
            platform = Platform.YOUTUBE,
            category = "Optimization",
            description = "Generate 3 contrasting psychological title variants to test with YouTube Test & Compare.",
            iconKey = "split",
            isPro = false,
            outputType = ToolOutputType.KEY_VALUE,
            keywords = listOf("ab test", "test and compare", "ctr", "variants"),
            fields = listOf(
                InputField.Text("topic", "Core Video Premise", "e.g., How to negotiate a 20% salary increase"),
                InputField.Text("target_audience", "Audience", "e.g., Corporate professionals and software engineers")
            )
        ),
        ToolConfig(
            id = "yt_chapters",
            name = "Video Chapters Generator",
            platform = Platform.YOUTUBE,
            category = "Optimization",
            description = "Generate structured 0:00 timestamp chapters with keyword-rich titles for Google search.",
            iconKey = "list",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("chapters", "timestamps", "google search", "seo"),
            fields = listOf(
                InputField.TextArea("script_or_summary", "Video Outline or Key Timestamps", "List topics covered in chronological order"),
                InputField.Select("video_length", "Approx Video Length", listOf("5-10 Minutes", "10-20 Minutes", "30-60 Minutes", "60+ Minutes"))
            )
        ),
        ToolConfig(
            id = "yt_pinned_comment",
            name = "Pinned Comment Generator",
            platform = Platform.YOUTUBE,
            category = "Optimization",
            description = "High-engagement pinned comment templates that trigger replies, debate, and resource clicks.",
            iconKey = "pin",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("pinned comment", "engagement", "replies", "algorithm"),
            fields = listOf(
                InputField.Text("video_topic", "Video Topic", "e.g., Apple M4 Mac Mini Review"),
                InputField.Select("goal", "Comment Objective", listOf("Spark Fiery Discussion / Debate", "Poll Viewer Opinion (A vs B)", "Offer Free Resource Download", "Ask for Feedback on Next Video"))
            )
        ),
        ToolConfig(
            id = "yt_community_post",
            name = "Community Post Generator",
            platform = Platform.YOUTUBE,
            category = "Optimization",
            description = "Polls, behind-the-scenes teasers, and text updates for the YouTube Community tab.",
            iconKey = "community",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("community tab", "poll", "teaser", "engagement"),
            fields = listOf(
                InputField.Select("post_type", "Community Post Format", listOf("Multiple Choice Poll", "Image / Teaser Post", "Question for Next Q&A", "Milestone Celebration")),
                InputField.Text("topic", "Upcoming Video or Topic", "e.g., Which camera should I review next?")
            )
        ),
        ToolConfig(
            id = "yt_end_screen_cta",
            name = "End Screen CTA Generator",
            platform = Platform.YOUTUBE,
            category = "Optimization",
            description = "Final 20-second vocal script & visual overlay prompts to maximize end screen CTR.",
            iconKey = "endscreen",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("end screen", "ctr", "card", "subscribe"),
            fields = listOf(
                InputField.Text("linked_video", "Title of Video Being Recommended", "e.g., Master Color Grading in 10 Minutes"),
                InputField.Select("urgency", "CTA Urgency Level", listOf("Natural Continuation (\"Watch this next\")", "Curiosity Loop (\"Here's the mistake you're making\")", "Direct Series Part 2"))
            )
        ),
        ToolConfig(
            id = "yt_sponsorship_pitch",
            name = "Sponsorship Pitch Generator",
            platform = Platform.YOUTUBE,
            category = "Optimization",
            description = "Professional email pitch to secure paid brand sponsorships and free product review units.",
            iconKey = "mail",
            isPro = true,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("sponsorship", "brand deal", "email", "pitch"),
            fields = listOf(
                InputField.Text("channel_name", "Your Channel Name", "e.g., TechUncut"),
                InputField.Text("brand_name", "Target Brand / Company", "e.g., NordVPN / Anker"),
                InputField.Text("subscriber_count", "Audience Stats", "e.g., 25,000 subscribers, 80k avg views/month"),
                InputField.Text("product", "Specific Product or Campaign", "e.g., New Wireless Microphone review integration")
            )
        ),
        ToolConfig(
            id = "yt_repurposing",
            name = "Video Repurposing Generator",
            platform = Platform.YOUTUBE,
            category = "Optimization",
            description = "Turn one YouTube video transcript into 3 Shorts, a Twitter/X thread, and an email newsletter.",
            iconKey = "repurpose",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("repurpose", "shorts", "newsletter", "twitter thread"),
            fields = listOf(
                InputField.Text("title", "Original YouTube Video Title", "e.g., 7 Stoic Rules for High Performance"),
                InputField.TextArea("transcript_summary", "Core Key Takeaways", "Summarize the 3-5 main lessons")
            )
        ),
        ToolConfig(
            id = "yt_content_strategy",
            name = "YouTube Content Strategy Generator",
            platform = Platform.YOUTUBE,
            category = "Optimization",
            description = "Comprehensive 90-day channel blueprint covering positioning, packaging, and monetization.",
            iconKey = "strategy",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("strategy", "blueprint", "growth", "roadmap"),
            fields = listOf(
                InputField.Text("channel_niche", "Channel Niche", "e.g., AI Automation for Small Business"),
                InputField.Text("current_status", "Current Channel Size", "e.g., 500 subscribers, starting out"),
                InputField.Text("target_goal", "6-Month Target", "e.g., 10,000 subscribers & $2,000/mo income")
            )
        )
    )
}
