package com.example.ui.theme

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * TubeMaster AI — Controlled Premium Motion System
 *
 * Provides calibrated durations, physics curves, and transitions
 * that make the interface responsive without being distracting.
 */
object MotionDurations {
    const val INSTANT = 100
    const val FAST = 160
    const val BASE = 220
    const val MODERATE = 280
    const val DELIBERATE = 360
}

object MotionCurves {
    val Standard = FastOutSlowInEasing
    val Decelerate = LinearOutSlowInEasing
    val Accelerate = FastOutLinearInEasing
    val PremiumSnappy = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
    val SubtleSpring = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
}

/**
 * Standard screen transition preset
 */
fun pageEnterTransition(
    slideDistance: Int = 40,
    durationMs: Int = MotionDurations.BASE
): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = durationMs,
            easing = MotionCurves.Decelerate
        )
    ) + slideInVertically(
        animationSpec = tween(
            durationMillis = durationMs,
            easing = MotionCurves.PremiumSnappy
        ),
        initialOffsetY = { slideDistance }
    )
}

fun pageExitTransition(
    slideDistance: Int = 30,
    durationMs: Int = MotionDurations.FAST
): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = durationMs,
            easing = MotionCurves.Accelerate
        )
    ) + slideOutVertically(
        animationSpec = tween(
            durationMillis = durationMs,
            easing = MotionCurves.Accelerate
        ),
        targetOffsetY = { -slideDistance }
    )
}

/**
 * Horizontal tab / stage slide transition
 */
fun horizontalStageTransition(
    forward: Boolean = true,
    durationMs: Int = MotionDurations.BASE
): ContentTransform {
    val enter = fadeIn(tween(durationMs, easing = MotionCurves.Decelerate)) +
            slideInHorizontally(
                animationSpec = tween(durationMs, easing = MotionCurves.PremiumSnappy),
                initialOffsetX = { if (forward) it / 6 else -it / 6 }
            )
    val exit = fadeOut(tween(MotionDurations.FAST, easing = MotionCurves.Accelerate)) +
            slideOutHorizontally(
                animationSpec = tween(MotionDurations.FAST, easing = MotionCurves.Accelerate),
                targetOffsetX = { if (forward) -it / 8 else it / 8 }
            )
    return enter togetherWith exit
}

/**
 * Modal dialog & bottom sheet pop-in preset
 */
fun modalEnterTransition(durationMs: Int = MotionDurations.BASE): EnterTransition {
    return fadeIn(
        animationSpec = tween(durationMs, easing = MotionCurves.Decelerate)
    ) + scaleIn(
        initialScale = 0.94f,
        animationSpec = tween(durationMs, easing = MotionCurves.PremiumSnappy)
    )
}

fun modalExitTransition(durationMs: Int = MotionDurations.FAST): ExitTransition {
    return fadeOut(
        animationSpec = tween(durationMs, easing = MotionCurves.Accelerate)
    ) + scaleOut(
        targetScale = 0.94f,
        animationSpec = tween(durationMs, easing = MotionCurves.Accelerate)
    )
}

/**
 * Modifier extension for interactive tactile press feedback with scale & alpha
 */
@Composable
fun Modifier.premiumClickable(
    enabled: Boolean = true,
    pressedScale: Float = 0.97f,
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) pressedScale else 1f,
        animationSpec = tween(durationMillis = MotionDurations.FAST, easing = MotionCurves.Decelerate),
        label = "press_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.88f else 1f,
        animationSpec = tween(durationMillis = MotionDurations.FAST, easing = MotionCurves.Decelerate),
        label = "press_alpha"
    )

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}
