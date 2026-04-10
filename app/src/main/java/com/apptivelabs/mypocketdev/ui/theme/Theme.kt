package com.apptivelabs.mypocketdev.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Careem-inspired palette — warm, professional, techy
val Emerald = Color(0xFF0B8043)
val EmeraldDark = Color(0xFF066030)
val EmeraldLight = Color(0xFFE8F5E9)
val Surface = Color(0xFF1A1B1E)
val SurfaceVariant = Color(0xFF232427)
val CardBg = Color(0xFF2A2B2F)
val TextPrimary = Color(0xFFF0F0F0)
val TextSecondary = Color(0xFFA0A3A8)
val SeverityCritical = Color(0xFFEF5350)
val SeverityHigh = Color(0xFFFF7043)
val SeverityMedium = Color(0xFFFFCA28)
val SeverityLow = Color(0xFF66BB6A)

private val DarkColorScheme = darkColorScheme(
    primary = Emerald,
    onPrimary = Color.White,
    primaryContainer = EmeraldDark,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    background = Surface
)

@Composable
fun MyPocketDevTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(), // Use default M3 typography
        content = content
    )
}
