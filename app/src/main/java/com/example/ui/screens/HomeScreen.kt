package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UsageManager
import com.example.data.registry.ToolRegistry
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.model.Platform
import com.example.model.ToolConfig
import com.example.ui.components.SearchBar
import com.example.ui.components.ToolCard
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen

@Composable
fun HomeScreen(
    isPro: Boolean,
    dailyCount: Int,
    limitReachedAt: Long? = null,
    searchQuery: String,
    language: AppLanguage,
    onSearchChange: (String) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onPlatformSelect: (Platform) -> Unit,
    onToolClick: (ToolConfig) -> Unit,
    onUpgradeClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero Section
        item {
            HeroBanner(
                isPro = isPro,
                language = language,
                onExploreClick = { onNavigate(AppScreen.TOOLS) },
                onUpgradeClick = onUpgradeClick
            )
        }

        // Daily Usage Card (for Free tier) or Pro Stats Card
        item {
            UsageStatusCard(
                isPro = isPro,
                dailyCount = dailyCount,
                limitReachedAt = limitReachedAt,
                language = language,
                onUpgradeClick = onUpgradeClick
            )
        }

        // Quick Search Bar
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        onSearchChange(it)
                        if (it.isNotBlank()) {
                            onNavigate(AppScreen.TOOLS)
                        }
                    },
                    placeholder = Translations.get("home.search_placeholder", language)
                )
            }
        }

        // Platform Hub Cards (YouTube 40, Instagram 30, Facebook 30)
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Translations.get("home.platform_toolkits", language),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = Translations.get("home.total_tools", language),
                        fontSize = 12.sp,
                        color = PrimaryRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PlatformOverviewCard(
                        title = "YouTube",
                        count = 40,
                        accentColor = YouTubeRed,
                        tagline = Translations.get("home.yt_tagline", language),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onPlatformSelect(Platform.YOUTUBE)
                            onNavigate(AppScreen.TOOLS)
                        }
                    )
                    PlatformOverviewCard(
                        title = "Instagram",
                        count = 30,
                        accentColor = InstagramPink,
                        tagline = Translations.get("home.ig_tagline", language),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onPlatformSelect(Platform.INSTAGRAM)
                            onNavigate(AppScreen.TOOLS)
                        }
                    )
                    PlatformOverviewCard(
                        title = "Facebook",
                        count = 30,
                        accentColor = FacebookBlue,
                        tagline = Translations.get("home.fb_tagline", language),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onPlatformSelect(Platform.FACEBOOK)
                            onNavigate(AppScreen.TOOLS)
                        }
                    )
                }
            }
        }

        // Featured & Popular Tools Carousel
        item {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = PrimaryRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Translations.get("home.featured_tools", language),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = Translations.get("home.view_all", language),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryRed,
                        modifier = Modifier
                            .clickable { onNavigate(AppScreen.TOOLS) }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ToolRegistry.popularTools) { tool ->
                        ToolCard(
                            tool = tool,
                            isUserPro = isPro,
                            onClick = { onToolClick(tool) },
                            modifier = Modifier.width(260.dp)
                        )
                    }
                }
            }
        }

        // Pro Upgrade Callout (if on Free plan)
        if (!isPro) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                ProPromoCard(
                    language = language,
                    onUpgradeClick = onUpgradeClick
                )
            }
        }
    }
}

@Composable
fun HeroBanner(
    isPro: Boolean,
    language: AppLanguage,
    onExploreClick: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF22050B),
                        SurfaceDark,
                        PitchBlack
                    )
                )
            )
            .border(1.dp, PrimaryRed.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(PrimaryRed.copy(alpha = 0.15f))
                    .border(0.8.dp, PrimaryRed.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = Translations.get("home.hero_badge", language),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryRed,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = Translations.get("home.hero_title", language),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = Translations.get("home.hero_desc", language),
                fontSize = 13.sp,
                color = TextMuted,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onExploreClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(42.dp)
                ) {
                    Text(
                        text = Translations.get("home.explore_100", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                if (!isPro) {
                    Button(
                        onClick = onUpgradeClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700).copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(42.dp)
                            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = Translations.get("header.get_pro", language),
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UsageStatusCard(
    isPro: Boolean,
    dailyCount: Int,
    limitReachedAt: Long? = null,
    language: AppLanguage,
    onUpgradeClick: () -> Unit
) {
    val isLimitReached = dailyCount >= UsageManager.FREE_DAILY_LIMIT
    var remainingMillis by androidx.compose.runtime.remember(limitReachedAt) {
        val reached = limitReachedAt ?: 0L
        val elapsed = if (reached > 0L) System.currentTimeMillis() - reached else 0L
        val remaining = (UsageManager.ROLLING_WINDOW_MS - elapsed).coerceAtLeast(0L)
        androidx.compose.runtime.mutableLongStateOf(remaining)
    }

    androidx.compose.runtime.LaunchedEffect(limitReachedAt, isLimitReached) {
        if (isLimitReached && limitReachedAt != null && limitReachedAt > 0L) {
            while (true) {
                val elapsed = System.currentTimeMillis() - limitReachedAt
                val remaining = (UsageManager.ROLLING_WINDOW_MS - elapsed).coerceAtLeast(0L)
                remainingMillis = remaining
                kotlinx.coroutines.delay(10000L) // update every 10s
            }
        }
    }

    val hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(remainingMillis)
    val minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("usage_status_card"),
        colors = CardDefaults.cardColors(
            containerColor = if (isLimitReached && !isPro) Color(0xFF22050B) else SurfaceDark
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLimitReached && !isPro) PrimaryRed.copy(alpha = 0.6f) else BorderSubtle
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (isPro) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD700))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Translations.get("header.pro_active", language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = Translations.get("home.pro_active_desc", language),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLimitReached) {
                                Translations.get("home.limit_reached", language)
                            } else {
                                "${UsageManager.FREE_DAILY_LIMIT - dailyCount} ${Translations.get("home.generations_left", language)}"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isLimitReached) PrimaryRed else Color.White
                        )
                        Text(
                            text = if (isLimitReached) {
                                "${Translations.get("home.resets_in", language)} ${hours}h ${minutes}m"
                            } else {
                                "$dailyCount / ${UsageManager.FREE_DAILY_LIMIT} used"
                            },
                            fontSize = 12.sp,
                            fontWeight = if (isLimitReached) FontWeight.Bold else FontWeight.Normal,
                            color = if (isLimitReached) PrimaryRed else TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val progress = (dailyCount.toFloat() / UsageManager.FREE_DAILY_LIMIT).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (progress >= 1f) PrimaryRed else PrimaryRed.copy(alpha = 0.8f),
                        trackColor = SurfaceLighter,
                        strokeCap = StrokeCap.Round
                    )
                }
            }

            if (!isPro) {
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFD700).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .clickable { onUpgradeClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = Translations.get("home.go_pro", language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }
    }
}

@Composable
fun PlatformOverviewCard(
    title: String,
    count: Int,
    accentColor: Color,
    tagline: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.take(2).uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "$count Tools",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tagline,
                fontSize = 10.sp,
                color = TextMuted,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ProPromoCard(
    language: AppLanguage,
    onUpgradeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable { onUpgradeClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Translations.get("home.promo_title", language),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = Translations.get("home.promo_desc", language),
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD700)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Upgrade",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
