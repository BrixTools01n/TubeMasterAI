package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.ProGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TubeMasterRed
import com.example.ui.theme.TubeMasterRedGlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val logoScale = remember { Animatable(0.7f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0.2f) }

    LaunchedEffect(Unit) {
        // Logo animation
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        }
        launch {
            logoScale.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        }

        // Pulse glow
        launch {
            glowAlpha.animateTo(
                targetValue = 0.6f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }

        delay(400)
        textAlpha.animateTo(1f, animationSpec = tween(500))

        delay(300)
        subtitleAlpha.animateTo(1f, animationSpec = tween(400))

        delay(1100)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle Red Glow behind logo
        Box(
            modifier = Modifier
                .size(240.dp)
                .alpha(glowAlpha.value)
                .background(
                    Brush.radialGradient(
                        listOf(
                            TubeMasterRed.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Uploaded TubeMaster Logo Asset
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF000000))
                    .border(2.dp, TubeMasterRed.copy(alpha = 0.8f), RoundedCornerShape(26.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tubemaster_logo_1786862791934),
                    contentDescription = "TubeMaster AI Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Brand Name
            Text(
                text = "TubeMaster AI",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = 1.sp,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 100 Creator Tools Tagline
            Box(
                modifier = Modifier
                    .alpha(subtitleAlpha.value)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF14080B))
                    .border(1.dp, TubeMasterRed.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "100 CREATOR TOOLS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TubeMasterRedGlow,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}
