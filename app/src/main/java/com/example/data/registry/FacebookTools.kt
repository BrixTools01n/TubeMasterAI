package com.example.data.registry

import com.example.model.InputField
import com.example.model.Platform
import com.example.model.ToolConfig
import com.example.model.ToolOutputType

object FacebookTools {
    val list: List<ToolConfig> = listOf(
        // === Facebook Content (1-10) ===
        ToolConfig(
            id = "fb_post",
            name = "Facebook Post Generator",
            platform = Platform.FACEBOOK,
            category = "Content",
            description = "Engaging long-form story posts, status updates, and announcement copy.",
            iconKey = "post",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("post", "status", "long-form", "storytelling"),
            fields = listOf(
                InputField.Text("topic", "Post Topic / Message", "e.g., The biggest lesson I learned from failing my first business"),
                InputField.Select("format", "Post Format", listOf("Personal Story / Vulnerability", "Educational Listicle with Spacing", "Direct Announcement / Update", "Contrarian Industry Thought")),
                InputField.Toggle("include_emojis", "Use Readable Spacing & Emojis", defaultValue = true)
            )
        ),
        ToolConfig(
            id = "fb_caption",
            name = "Facebook Caption Generator",
            platform = Platform.FACEBOOK,
            category = "Content",
            description = "Photo and video captions designed for Facebook feed algorithms and shares.",
            iconKey = "caption",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("caption", "feed", "photo caption", "facebook"),
            fields = listOf(
                InputField.Text("photo_context", "Photo or Video Description", "e.g., Team photo at our annual workshop"),
                InputField.Select("tone", "Tone", listOf("Warm & Inspiring", "Professional & Proud", "Humorous & Casual", "Behind the Scenes"))
            )
        ),
        ToolConfig(
            id = "fb_bio",
            name = "Facebook Bio Generator",
            platform = Platform.FACEBOOK,
            category = "Content",
            description = "Short, punchy Page and Profile intro bio that establishes trust and authority.",
            iconKey = "user",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("bio", "intro", "page intro", "profile"),
            fields = listOf(
                InputField.Text("business_name", "Page / Brand Name", "e.g., Peak Horizon Digital"),
                InputField.Text("specialty", "Core Specialty / Service", "e.g., Helping local dental clinics get 30+ new patients/mo"),
                InputField.Select("style", "Style", listOf("Direct & Professional", "Friendly & Local", "Bold & Results-Oriented"))
            )
        ),
        ToolConfig(
            id = "fb_page_description",
            name = "Facebook Page Description Generator",
            platform = Platform.FACEBOOK,
            category = "Content",
            description = "SEO-rich 'About' section for your Facebook Business or Creator Page.",
            iconKey = "about",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("page description", "about us", "business page", "seo"),
            fields = listOf(
                InputField.Text("company_name", "Page / Company Name", "e.g., BlueWave Fitness Center"),
                InputField.TextArea("services", "Products, Services, or Mission", "24/7 gym access, certified trainers, sauna, HIIT classes"),
                InputField.Text("contact_info", "Hours & Location or Website", "e.g., Open 24/7 | Downtown Austin | www.bluewave.com")
            )
        ),
        ToolConfig(
            id = "fb_reel_script",
            name = "Facebook Reel Script Generator",
            platform = Platform.FACEBOOK,
            category = "Content",
            description = "High-retention Facebook Reels scripts catering to Facebook's demographic.",
            iconKey = "reels",
            isPro = false,
            outputType = ToolOutputType.SCRIPT,
            keywords = listOf("reels", "short video", "facebook reels", "script"),
            fields = listOf(
                InputField.Text("topic", "Reels Topic", "e.g., 3 garden hacks for higher tomato yield"),
                InputField.Select("duration", "Length", listOf("30 Seconds", "60 Seconds", "90 Seconds")),
                InputField.Select("hook", "Hook Angle", listOf("Practical Life Hack", "Before & After Transformation", "Story of Overcoming a Problem"))
            )
        ),
        ToolConfig(
            id = "fb_video_script",
            name = "Facebook Video Script Generator",
            platform = Platform.FACEBOOK,
            category = "Content",
            description = "3-5 minute horizontal video scripts optimized for Facebook Watch and feed autoplay.",
            iconKey = "video",
            isPro = true,
            outputType = ToolOutputType.SCRIPT,
            keywords = listOf("watch", "video script", "facebook watch", "in-stream ads"),
            fields = listOf(
                InputField.Text("topic", "Video Title / Premise", "e.g., How to restore rusty cast iron pans like brand new"),
                InputField.Select("structure", "Pacing Structure", listOf("Fast Visual Demonstration", "Step-by-Step DIY Tutorial", "Dramatic Story with Moral"))
            )
        ),
        ToolConfig(
            id = "fb_hook_generator",
            name = "Facebook Hook Generator",
            platform = Platform.FACEBOOK,
            category = "Content",
            description = "First two lines of text that force users to tap '... See More' on their feed.",
            iconKey = "hook",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("hook", "see more", "click through", "open loop"),
            fields = listOf(
                InputField.Text("post_idea", "Core Story / Topic", "e.g., Why I fired my highest paying client last week"),
                InputField.Select("angle", "Psychological Hook", listOf("Curiosity Gap", "Shocking Truth", "Counter-Intuitive Confession", "Vulnerable Story Opening")),
                InputField.Slider("count", "Hooks to Generate", min = 4f, max = 8f, defaultValue = 5f)
            )
        ),
        ToolConfig(
            id = "fb_story_generator",
            name = "Facebook Story Generator",
            platform = Platform.FACEBOOK,
            category = "Content",
            description = "24-hour visual story slides with engagement stickers and link prompts.",
            iconKey = "story",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("stories", "facebook story", "stickers", "daily"),
            fields = listOf(
                InputField.Text("theme", "Story Subject", "e.g., Behind the scenes packing orders"),
                InputField.Select("goal", "Goal", listOf("Direct Product Link Click", "Audience Reaction / Vote", "Casual Check-in"))
            )
        ),
        ToolConfig(
            id = "fb_content_idea",
            name = "Facebook Content Idea Generator",
            platform = Platform.FACEBOOK,
            category = "Content",
            description = "Diverse post concepts specifically tuned for Facebook community sharing dynamics.",
            iconKey = "idea",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("ideas", "facebook posts", "shares", "viral"),
            fields = listOf(
                InputField.Text("page_niche", "Page Niche / Industry", "e.g., Home Renovation & DIY"),
                InputField.Select("type", "Content Category", listOf("Nostalgia & Memories", "Helpful Tips / Checklists", "Community Debate", "Customer Spotlight"))
            )
        ),
        ToolConfig(
            id = "fb_poll_generator",
            name = "Facebook Poll Generator",
            platform = Platform.FACEBOOK,
            category = "Content",
            description = "High-engagement 2-option and multi-choice polls that blow up comments.",
            iconKey = "poll",
            isPro = false,
            outputType = ToolOutputType.KEY_VALUE,
            keywords = listOf("poll", "vote", "engagement", "reaction"),
            fields = listOf(
                InputField.Text("topic", "Poll Subject", "e.g., Working from home vs Returning to Office"),
                InputField.Select("poll_type", "Poll Format", listOf("Option A vs Option B", "Reaction Emoji Voting (👍 vs ❤️)", "Open Community Question"))
            )
        ),

        // === Facebook Engagement (11-20) ===
        ToolConfig(
            id = "fb_hashtag",
            name = "Facebook Hashtag Generator",
            platform = Platform.FACEBOOK,
            category = "Engagement",
            description = "Minimal, high-impact hashtags tailored for Facebook group and page discovery.",
            iconKey = "hashtag",
            isPro = false,
            outputType = ToolOutputType.TAGS,
            keywords = listOf("hashtags", "facebook search", "topic tags"),
            fields = listOf(
                InputField.Text("topic", "Post Topic", "e.g., Small Business Saturday Event"),
                InputField.Slider("count", "Hashtag Count", min = 3f, max = 8f, defaultValue = 5f)
            )
        ),
        ToolConfig(
            id = "fb_cta_generator",
            name = "Facebook CTA Generator",
            platform = Platform.FACEBOOK,
            category = "Engagement",
            description = "Share, comment, Tag-A-Friend, and Messenger call-to-actions.",
            iconKey = "bell",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("cta", "tag a friend", "share this", "messenger"),
            fields = listOf(
                InputField.Select("goal", "Desired User Action", listOf("Tag a Friend Who Needs This", "Share to Your Timeline / Group", "Send Us a Message on WhatsApp / Messenger", "Drop a Comment Below")),
                InputField.Slider("count", "Variants", min = 3f, max = 6f, defaultValue = 4f)
            )
        ),
        ToolConfig(
            id = "fb_comment_reply",
            name = "Comment Reply Generator",
            platform = Platform.FACEBOOK,
            category = "Engagement",
            description = "Friendly, helpful replies for business reviews, inquiries, and customer feedback.",
            iconKey = "chat",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("comment reply", "customer support", "page moderator", "reviews"),
            fields = listOf(
                InputField.Text("customer_comment", "Comment or Review", "e.g., Do you offer home delivery in North Chicago?"),
                InputField.Select("tone", "Response Tone", listOf("Helpful & Direct", "Warm & Grateful", "De-escalating / Polite Problem Solving"))
            )
        ),
        ToolConfig(
            id = "fb_engagement_question",
            name = "Engagement Question Generator",
            platform = Platform.FACEBOOK,
            category = "Engagement",
            description = "Open-ended conversation starters that encourage paragraph-long replies.",
            iconKey = "question",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("questions", "comments", "conversation", "engagement"),
            fields = listOf(
                InputField.Text("niche", "Page Topic", "e.g., Classic Cars & Muscle Vehicles"),
                InputField.Select("style", "Style", listOf("Nostalgia ('What was your first...')", "Pick One ('If you could only keep one...')", "Advice ('What's your #1 tip for...')"))
            )
        ),
        ToolConfig(
            id = "fb_viral_post",
            name = "Viral Post Generator",
            platform = Platform.FACEBOOK,
            category = "Engagement",
            description = "High-shareability text templates engineered to evoke deep emotional resonance or nostalgia.",
            iconKey = "flame",
            isPro = true,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("viral post", "shares", "emotional", "nostalgia"),
            fields = listOf(
                InputField.Text("core_message", "Core Message / Story", "e.g., A reminder that life is too short to work a job you hate"),
                InputField.Select("emotion", "Target Emotion", listOf("Inspirational Uplift", "Nostalgic Reflection", "Gratitude & Family", "Relatable Frustration"))
            )
        ),
        ToolConfig(
            id = "fb_community_post",
            name = "Community Post Generator",
            platform = Platform.FACEBOOK,
            category = "Engagement",
            description = "Member spotlights, weekly check-ins, and milestone celebrations for Groups.",
            iconKey = "community",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("community", "facebook group", "check in", "welcome"),
            fields = listOf(
                InputField.Text("group_name", "Group Name / Topic", "e.g., Austin Real Estate Investors"),
                InputField.Select("post_type", "Community Format", listOf("Weekly Wins Friday", "New Member Welcome & Introduction", "Ask Me Anything (AMA)", "Resource Share"))
            )
        ),
        ToolConfig(
            id = "fb_discussion_starter",
            name = "Discussion Starter Generator",
            platform = Platform.FACEBOOK,
            category = "Engagement",
            description = "Deep dive group discussion topics that spark hundreds of authentic comments.",
            iconKey = "chat",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("discussion", "debate", "group post", "starter"),
            fields = listOf(
                InputField.Text("industry", "Industry / Topic", "e.g., Freelance Copywriting"),
                InputField.Select("angle", "Debate Angle", listOf("Hourly vs Value-Based Pricing", "AI Tools Impact on Jobs", "Biggest Client Red Flags"))
            )
        ),
        ToolConfig(
            id = "fb_audience_question",
            name = "Audience Question Generator",
            platform = Platform.FACEBOOK,
            category = "Engagement",
            description = "Market research questions to uncover what products or services your followers need.",
            iconKey = "target",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("market research", "customer discovery", "questions"),
            fields = listOf(
                InputField.Text("service_idea", "What You Offer / Teach", "e.g., Online Yoga Classes for Back Pain"),
                InputField.Select("focus", "What to Learn", listOf("Biggest Frustrations / Obstacles", "Price Sensitivity & Budget", "Preferred Format (Live vs Recorded)"))
            )
        ),
        ToolConfig(
            id = "fb_giveaway_post",
            name = "Giveaway Post Generator",
            platform = Platform.FACEBOOK,
            category = "Engagement",
            description = "Compliant Facebook contest & giveaway copy with clear entry rules and disclaimer.",
            iconKey = "gift",
            isPro = true,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("giveaway", "contest", "free prize", "entry rules"),
            fields = listOf(
                InputField.Text("prize", "Prize Description", "e.g., $250 Amazon Gift Card + Deluxe Spa Bundle"),
                InputField.Text("company_name", "Host Brand", "e.g., Serenity Wellness"),
                InputField.Text("deadline", "End Date & Time", "e.g., Sunday at 11:59 PM EST")
            )
        ),
        ToolConfig(
            id = "fb_contest_post",
            name = "Contest Post Generator",
            platform = Platform.FACEBOOK,
            category = "Engagement",
            description = "User-generated content contests (photo submissions, best caption, storytelling).",
            iconKey = "award",
            isPro = true,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("contest", "ugc", "photo contest", "competition"),
            fields = listOf(
                InputField.Text("contest_theme", "Contest Theme", "e.g., Cutest Dog Photo of the Month"),
                InputField.Text("reward", "Reward / Prize", "e.g., Featured on billboard + $100 PetSmart Card"),
                InputField.Select("entry_method", "How to Enter", listOf("Post Photo in Comments", "Tag Our Page in Timeline Post", "Vote on Finalists"))
            )
        ),

        // === Facebook Growth & Marketing (21-30) ===
        ToolConfig(
            id = "fb_page_name",
            name = "Facebook Page Name Generator",
            platform = Platform.FACEBOOK,
            category = "Growth & Marketing",
            description = "Brandable, professional names optimized for Facebook search and local businesses.",
            iconKey = "brand",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("page name", "business name", "branding", "facebook page"),
            fields = listOf(
                InputField.Text("service", "Business / Service", "e.g., Eco-Friendly House Cleaning"),
                InputField.Text("city", "City / Location (optional)", "e.g., Seattle, WA"),
                InputField.Select("style", "Vibe", listOf("Modern & Eco", "Trustworthy & Established", "Fast & Friendly"))
            )
        ),
        ToolConfig(
            id = "fb_group_name",
            name = "Facebook Group Name Generator",
            platform = Platform.FACEBOOK,
            category = "Growth & Marketing",
            description = "High-search keyword optimized group names that attract targeted organic members.",
            iconKey = "users",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("group name", "community name", "members", "organic traffic"),
            fields = listOf(
                InputField.Text("target_member", "Target Member / Topic", "e.g., Shopify Store Owners & E-com Builders"),
                InputField.Select("format", "Group Framing", listOf("Mastermind & Networking", "Free Support & Tips", "Local Buy / Sell / Trade", "Job Board & Hiring"))
            )
        ),
        ToolConfig(
            id = "fb_group_description",
            name = "Facebook Group Description Generator",
            platform = Platform.FACEBOOK,
            category = "Growth & Marketing",
            description = "Group About section with membership guidelines, rules, and promotional policy.",
            iconKey = "document",
            isPro = false,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("group description", "rules", "guidelines", "moderation"),
            fields = listOf(
                InputField.Text("group_name", "Group Name", "e.g., SaaS Founders & Bootstrappers"),
                InputField.TextArea("group_purpose", "Mission & Who it's for", "A place for bootstrapped software founders to share revenue milestones, code, and growth hacks"),
                InputField.Toggle("strict_rules", "Include Strict No-Self-Promo Rule", defaultValue = true)
            )
        ),
        ToolConfig(
            id = "fb_content_calendar",
            name = "Facebook Content Calendar",
            platform = Platform.FACEBOOK,
            category = "Growth & Marketing",
            description = "Monthly Facebook posting strategy balancing text, images, Reels, and Live events.",
            iconKey = "calendar",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("calendar", "schedule", "monthly plan", "page growth"),
            fields = listOf(
                InputField.Text("business_type", "Business / Niche", "e.g., Local Italian Restaurant"),
                InputField.Select("posting_frequency", "Posts Per Week", listOf("Daily (7 posts/wk)", "5 Posts / Week", "3 High-Quality Posts / Week"))
            )
        ),
        ToolConfig(
            id = "fb_growth_strategy",
            name = "Facebook Growth Strategy Generator",
            platform = Platform.FACEBOOK,
            category = "Growth & Marketing",
            description = "Organic funnel system connecting Page posts, Group building, and Messenger leads.",
            iconKey = "strategy",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("growth strategy", "lead generation", "organic funnel", "messenger"),
            fields = listOf(
                InputField.Text("offer", "Primary Offer / Service", "e.g., Solar Panel Installation for Homeowners"),
                InputField.Select("target_budget", "Marketing Route", listOf("100% Organic (Groups + Reels)", "Hybrid Organic + $5/day Boosts", "Paid Lead Gen Ads"))
            )
        ),
        ToolConfig(
            id = "fb_audience_persona",
            name = "Facebook Audience Persona Generator",
            platform = Platform.FACEBOOK,
            category = "Growth & Marketing",
            description = "Targeting breakdown including Facebook interest clusters, behaviors, and demographics.",
            iconKey = "users",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("persona", "interest targeting", "ad targeting", "demographics"),
            fields = listOf(
                InputField.Text("product", "Product / Solution", "e.g., Ergonomic Office Chairs"),
                InputField.Select("target_tier", "Buyer Profile", listOf("Remote Tech Workers", "Corporate Purchasing Managers", "Gaming & Streamers"))
            )
        ),
        ToolConfig(
            id = "fb_ad_copy",
            name = "Facebook Ad Copy Generator",
            platform = Platform.FACEBOOK,
            category = "Growth & Marketing",
            description = "High-converting ad copy using PAS (Problem-Agitate-Solution) and AIDA frameworks.",
            iconKey = "ad",
            isPro = true,
            outputType = ToolOutputType.TEXT,
            keywords = listOf("ad copy", "meta ads", "pas", "aida", "conversion"),
            fields = listOf(
                InputField.Text("product_name", "Product / Offer", "e.g., AI Video Editor App"),
                InputField.TextArea("benefits", "Top 3 Key Benefits", "1. Auto generates subtitles in 10s 2. Adds b-roll automatically 3. Exports 4k"),
                InputField.Select("framework", "Copywriting Framework", listOf("PAS (Problem, Agitate, Solve)", "AIDA (Attention, Interest, Desire, Action)", "Before-After-Bridge (BAB)", "Direct Offer / Discount"))
            )
        ),
        ToolConfig(
            id = "fb_ad_headline",
            name = "Facebook Ad Headline Generator",
            platform = Platform.FACEBOOK,
            category = "Growth & Marketing",
            description = "20 high-CTR headlines for the Meta Ads Manager primary text & headline fields.",
            iconKey = "title",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("ad headline", "meta ads", "ctr", "facebook ads"),
            fields = listOf(
                InputField.Text("offer", "Core Offer / Promo", "e.g., 50% Off First Month of Meal Delivery"),
                InputField.Slider("count", "Headlines to Generate", min = 5f, max = 15f, defaultValue = 10f)
            )
        ),
        ToolConfig(
            id = "fb_marketing_idea",
            name = "Facebook Marketing Idea Generator",
            platform = Platform.FACEBOOK,
            category = "Growth & Marketing",
            description = "Creative campaign concepts, viral stunts, local partnerships, and holiday promos.",
            iconKey = "idea",
            isPro = false,
            outputType = ToolOutputType.LIST,
            keywords = listOf("marketing", "campaigns", "promo ideas", "growth"),
            fields = listOf(
                InputField.Text("business", "Business Type", "e.g., Boutique Fitness Studio"),
                InputField.Select("campaign_season", "Timing / Season", listOf("New Year / Fresh Start", "Summer Challenge", "Black Friday / Holiday", "Year-round Evergreen"))
            )
        ),
        ToolConfig(
            id = "fb_content_repurposer",
            name = "Facebook Content Repurposer",
            platform = Platform.FACEBOOK,
            category = "Growth & Marketing",
            description = "Repurpose blog posts, YouTube videos, or podcast notes into 3 engaging Facebook posts.",
            iconKey = "repurpose",
            isPro = true,
            outputType = ToolOutputType.STRATEGY,
            keywords = listOf("repurpose", "blog to post", "content multiplier", "facebook"),
            fields = listOf(
                InputField.Text("original_title", "Source Content Title", "e.g., 10 Tips for First-Time Homebuyers"),
                InputField.TextArea("content_summary", "Main Points / Text", "Paste key points from article or video...")
            )
        )
    )
}
