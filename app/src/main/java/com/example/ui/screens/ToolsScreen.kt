package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.registry.ToolRegistry
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.model.Platform
import com.example.model.ToolConfig
import com.example.ui.components.CategoryPills
import com.example.ui.components.PlatformTabBar
import com.example.ui.components.SearchBar
import com.example.ui.components.ToolCard
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TubeMasterRed

@Composable
fun ToolsScreen(
    selectedPlatform: Platform?,
    selectedCategory: String,
    searchQuery: String,
    isPro: Boolean,
    language: AppLanguage,
    onPlatformSelect: (Platform?) -> Unit,
    onCategorySelect: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onToolClick: (ToolConfig) -> Unit
) {
    val filteredTools = remember(selectedPlatform, selectedCategory, searchQuery) {
        ToolRegistry.searchTools(
            query = searchQuery,
            platform = selectedPlatform,
            category = selectedCategory
        )
    }

    val availableCategories = remember(selectedPlatform) {
        ToolRegistry.getCategoriesForPlatform(selectedPlatform)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("tools_screen")
    ) {
        // Platform Filter Bar (All 100, YouTube 40, Instagram 30, Facebook 30)
        PlatformTabBar(
            selectedPlatform = selectedPlatform,
            onSelect = onPlatformSelect
        )

        // Category Pills Filter
        CategoryPills(
            categories = availableCategories,
            selectedCategory = selectedCategory,
            onSelect = onCategorySelect
        )

        // Search Bar
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchChange,
                placeholder = Translations.get("home.search_placeholder", language)
            )
        }

        // Status / Count Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when {
                    searchQuery.isNotBlank() -> "${Translations.get("tools.search_results", language)} \"$searchQuery\" (${filteredTools.size})"
                    selectedPlatform != null -> "${selectedPlatform.displayName} ${Translations.get("tools.showing_tools", language)} (${filteredTools.size})"
                    else -> "${Translations.get("tools.all_tools", language)} (${filteredTools.size})"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            if (selectedCategory != "All") {
                Text(
                    text = selectedCategory,
                    fontSize = 11.sp,
                    color = TubeMasterRed
                )
            }
        }

        // Tools List
        if (filteredTools.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = Translations.get("tools.no_tools", language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Translations.get("tools.no_tools_sub", language),
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = {
                        onSearchChange("")
                        onCategorySelect("All")
                        onPlatformSelect(null)
                    }) {
                        Text(
                            text = Translations.get("tools.clear_filters", language),
                            color = TubeMasterRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTools, key = { it.id }) { tool ->
                    ToolCard(
                        tool = tool,
                        isUserPro = isPro,
                        onClick = { onToolClick(tool) }
                    )
                }
            }
        }
    }
}
