package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ProGold
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TubeMasterRed
import com.example.viewmodel.AppScreen

data class NavItem(
    val screen: AppScreen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null,
    val badgeText: String? = null
)

@Composable
fun AppBottomBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    savedCount: Int,
    isPro: Boolean,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem(
            screen = AppScreen.HOME,
            label = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        NavItem(
            screen = AppScreen.TOOLS,
            label = "Tools",
            selectedIcon = Icons.Filled.Widgets,
            unselectedIcon = Icons.Outlined.Widgets,
            badgeText = "100"
        ),
        NavItem(
            screen = AppScreen.SAVED,
            label = "Saved",
            selectedIcon = Icons.Filled.Bookmark,
            unselectedIcon = Icons.Outlined.BookmarkBorder,
            badgeCount = if (savedCount > 0) savedCount else null
        ),
        NavItem(
            screen = AppScreen.PRICING,
            label = "Pricing",
            selectedIcon = Icons.Filled.Star,
            unselectedIcon = Icons.Outlined.StarOutline,
            badgeText = if (isPro) "PRO" else null
        ),
        NavItem(
            screen = AppScreen.PROFILE,
            label = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .border(
                width = 1.dp,
                color = SurfaceBorder,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = currentScreen == item.screen
                val tint = if (isSelected) TubeMasterRed else TextMuted

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(item.screen) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .testTag("nav_item_${item.screen.name.lowercase()}")
                ) {
                    BadgedBox(
                        badge = {
                            if (item.badgeText != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (item.badgeText == "PRO") ProGold else TubeMasterRed)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = item.badgeText,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.badgeText == "PRO") Color.Black else Color.White
                                    )
                                }
                            } else if (item.badgeCount != null) {
                                Badge(
                                    containerColor = TubeMasterRed,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = item.badgeCount.toString(),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            tint = tint,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) TextPrimary else TextMuted
                    )
                }
            }
        }
    }
}
