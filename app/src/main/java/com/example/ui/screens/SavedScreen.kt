package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SavedItemEntity
import com.example.data.registry.ToolRegistry
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.model.Platform
import com.example.model.ToolConfig
import com.example.ui.components.PlatformBadge
import com.example.ui.components.SearchBar
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.ProGold
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TubeMasterRed
import com.example.viewmodel.AppScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedScreen(
    savedItems: List<SavedItemEntity>,
    language: AppLanguage,
    onCopy: (String, String) -> Unit,
    onDelete: (Long) -> Unit,
    onToggleFavorite: (SavedItemEntity) -> Unit,
    onClearAll: () -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onOpenTool: (ToolConfig) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var showClearDialog by remember { mutableStateOf(false) }

    val filteredItems = remember(savedItems, searchQuery, selectedFilter) {
        savedItems.filter { item ->
            val matchesFilter = when (selectedFilter) {
                "Favorites" -> item.isFavorite
                "YouTube" -> item.platform == Platform.YOUTUBE.name
                "Instagram" -> item.platform == Platform.INSTAGRAM.name
                "Facebook" -> item.platform == Platform.FACEBOOK.name
                else -> true
            }

            val matchesSearch = if (searchQuery.isBlank()) true else {
                val q = searchQuery.trim().lowercase()
                item.title.lowercase().contains(q) ||
                item.promptSummary.lowercase().contains(q) ||
                item.content.lowercase().contains(q) ||
                item.toolName.lowercase().contains(q)
            }

            matchesFilter && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("saved_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = Translations.get("saved.title", language),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${savedItems.size} ${Translations.get("saved.stored_locally", language)}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            if (savedItems.isNotEmpty()) {
                TextButton(onClick = { showClearDialog = true }) {
                    Text(
                        text = Translations.get("saved.clear_all", language),
                        fontSize = 12.sp,
                        color = Color(0xFFEF4444)
                    )
                }
            }
        }

        // Search in saved
        if (savedItems.isNotEmpty()) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = Translations.get("saved.search_placeholder", language)
                )
            }

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Favorites", "YouTube", "Instagram", "Facebook").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) TubeMasterRed else SurfaceCard)
                            .border(1.dp, if (isSelected) TubeMasterRed else SurfaceBorder, RoundedCornerShape(16.dp))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        // Content List
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (savedItems.isEmpty()) Translations.get("saved.empty_title", language) else "No matching items",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (savedItems.isEmpty())
                            Translations.get("saved.empty_desc", language)
                        else "Try a different search keyword or filter tab.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onNavigate(AppScreen.TOOLS) },
                        colors = ButtonDefaults.buttonColors(containerColor = TubeMasterRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = Translations.get("home.explore_100", language),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    SavedItemCard(
                        item = item,
                        language = language,
                        onCopy = { onCopy(item.content, Translations.get("toast.copied", language)) },
                        onDelete = { onDelete(item.id) },
                        onToggleFavorite = { onToggleFavorite(item) },
                        onOpenTool = {
                            val tool = ToolRegistry.getToolById(item.toolId)
                            if (tool != null) onOpenTool(tool)
                        }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Saved Items?", color = TextPrimary) },
            text = { Text("Are you sure you want to delete all saved creations? This action cannot be undone.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAll()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = SurfaceCardElevated
        )
    }
}

@Composable
fun SavedItemCard(
    item: SavedItemEntity,
    language: AppLanguage,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenTool: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val platformEnum = try {
        Platform.valueOf(item.platform)
    } catch (e: Exception) {
        Platform.YOUTUBE
    }

    val dateFormatted = remember(item.timestamp) {
        SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(item.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlatformBadge(platform = platformEnum)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (item.isFavorite) ProGold else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tool and Prompt
            Text(
                text = item.toolName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Input: ${item.promptSummary}",
                fontSize = 12.sp,
                color = TextMuted,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Content preview / full
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .padding(10.dp)
            ) {
                Text(
                    text = item.content,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 3
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Footer with date and expand toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormatted,
                    fontSize = 10.sp,
                    color = TextMuted
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { isExpanded = !isExpanded }
                        .padding(4.dp)
                ) {
                    Text(
                        text = if (isExpanded) "Show Less" else "Expand Full",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TubeMasterRed
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = TubeMasterRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
