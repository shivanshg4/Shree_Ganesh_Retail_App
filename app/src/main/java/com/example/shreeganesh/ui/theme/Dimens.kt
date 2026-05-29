package com.example.shreeganesh.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Spacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val base: Dp = 12.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    
    val gutter: Dp = 24.dp,
    val cardPadding: Dp = 24.dp,
    val marginEdge: Dp = 32.dp,
    
    // Touch targets
    val touchTargetMin: Dp = 48.dp,
    val touchTargetStandard: Dp = 64.dp,
    val listItemMinHeight: Dp = 72.dp
)

@Immutable
data class Radius(
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val pill: Dp = 100.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
val LocalRadius = staticCompositionLocalOf { Radius() }
