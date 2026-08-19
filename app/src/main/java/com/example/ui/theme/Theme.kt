package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TubeMasterColorScheme = darkColorScheme(
    primary = TubeMasterRed,
    onPrimary = Color.White,
    primaryContainer = TubeMasterRedSurface,
    onPrimaryContainer = TubeMasterRedGlow,
    
    secondary = TubeMasterRedGlow,
    onSecondary = Color.White,
    secondaryContainer = SurfaceCardElevated,
    onSecondaryContainer = TextPrimary,
    
    tertiary = ProGold,
    onTertiary = Color.Black,
    tertiaryContainer = ProGoldSurface,
    onTertiaryContainer = ProGold,
    
    background = BackgroundDark,
    onBackground = TextPrimary,
    
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    
    outline = SurfaceBorder,
    outlineVariant = SurfaceBorderActive,
    
    error = Color(0xFFEF4444),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TubeMasterColorScheme,
        typography = Typography,
        content = content
    )
}
