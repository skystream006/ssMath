package com.ssmath.app

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp

/** Color themes offered when the Blue Wave appearance is off, matching ssMusic Player. */
val COLOR_THEMES = listOf("midnight" to 0xFF5F7FF0, "royal-purple" to 0xFF7139C6,
    "gold" to 0xFFA77A0A, "green" to 0xFF227452, "pink" to 0xFFBF3D78, "black" to 0xFF202124)

fun colorThemeFromPreference(value: String?): String = COLOR_THEMES.firstOrNull { it.first == value }?.first ?: "green"

/** [mode] is "dark", "light" or null to follow the theme's default (dark for midnight and black). */
fun isDarkTheme(theme: String, mode: String?): Boolean =
    mode == "dark" || (mode == null && theme in listOf("midnight", "black"))

@Composable
fun MathTheme(theme: String = "green", mode: String? = null, waveAppearance: Boolean = true,
    skin: AppSkin? = null, content: @Composable () -> Unit) {
    val accent = when (theme) {
        "midnight" -> Color(0xFF5F7FF0)
        "royal-purple" -> Color(0xFF7139C6)
        "gold" -> Color(0xFF8C6600)
        "pink" -> Color(0xFFBF3D78)
        else -> Color(0xFF227452)
    }
    val dark = isDarkTheme(theme, mode)
    val colors = if (waveAppearance) darkColorScheme(
        primary = Color(0xFF68DEFF), onPrimary = Color(0xFF00212D),
        primaryContainer = Color(0xFF1049A2), onPrimaryContainer = Color(0xFFF0F6FF),
        secondary = Color(0xFFB5C9E8), onSecondary = Color(0xFF162336),
        secondaryContainer = Color(0xFF192D46), onSecondaryContainer = Color(0xFFD8E7FF),
        tertiary = Color(0xFFB9D2C8), onTertiary = Color(0xFF132820),
        background = Color(0xFF030508), onBackground = Color(0xFFEDF3FC),
        surface = Color(0xFF080C12), onSurface = Color(0xFFEDF3FC),
        surfaceDim = Color(0xFF06090E), surfaceBright = Color(0xFF252E3A),
        surfaceContainerLowest = Color(0xFF030508), surfaceContainerLow = Color(0xFF0B1018),
        surfaceContainer = Color(0xFF101720), surfaceContainerHigh = Color(0xFF17212D),
        surfaceContainerHighest = Color(0xFF202D3E),
        surfaceVariant = Color(0xFF1A2636), onSurfaceVariant = Color(0xFFA5B6CC),
        outline = Color(0xFF55667D), outlineVariant = Color(0xFF253347)
    ) else if (theme == "black") blackColorScheme(dark)
    else if (dark) darkColorScheme(primary = accent.copy(alpha = 1f), secondary = Color(0xFFF3B6AA),
        background = Color(0xFF141817), surface = Color(0xFF191E1C), onPrimary = Color.White)
    else lightColorScheme(primary = accent, secondary = Color(0xFF9F4C3B), background = Color(0xFFF6F8F6), surface = Color(0xFFF6F8F6))
    CompositionLocalProvider(LocalAppSkin provides skin) {
        MaterialTheme(colorScheme = colors,
            shapes = Shapes(extraSmall = RoundedCornerShape(4.dp), small = RoundedCornerShape(6.dp),
                medium = RoundedCornerShape(8.dp), large = RoundedCornerShape(8.dp), extraLarge = RoundedCornerShape(8.dp)),
            content = content)
    }
}

private fun blackColorScheme(dark: Boolean): ColorScheme {
    val accent = if (dark) Color(0xFFF5D442) else Color(0xFF806000)
    val paper = if (dark) Color(0xFF0E0E0E) else Color(0xFFF5F5F5)
    val surface = if (dark) Color(0xFF181818) else Color.White
    val raised = if (dark) Color(0xFF262626) else Color(0xFFEAEAEA)
    val field = if (dark) Color(0xFF141414) else Color(0xFFFAFAFA)
    val ink = if (dark) Color(0xFFF0F0EE) else Color(0xFF202124)
    val muted = if (dark) Color(0xFFADADAD) else Color(0xFF686868)
    val line = if (dark) Color(0xFF373737) else Color(0xFFD8D8D8)
    val onAccent = if (dark) Color(0xFF141414) else Color.White
    val danger = if (dark) Color(0xFFFF9AAB) else Color(0xFFB03250)
    val selection = lerp(surface, accent, 0.12f)
    return (if (dark) darkColorScheme() else lightColorScheme()).copy(
        primary = accent, onPrimary = onAccent,
        primaryContainer = selection, onPrimaryContainer = accent, inversePrimary = if (dark) Color(0xFF806000) else Color(0xFFF5D442),
        secondary = accent, onSecondary = onAccent,
        secondaryContainer = selection, onSecondaryContainer = accent,
        tertiary = accent, onTertiary = onAccent,
        tertiaryContainer = selection, onTertiaryContainer = accent,
        background = paper, onBackground = ink,
        surface = surface, onSurface = ink, surfaceTint = accent,
        surfaceDim = if (dark) paper else raised, surfaceBright = if (dark) raised else surface,
        surfaceContainerLowest = paper, surfaceContainerLow = field,
        surfaceContainer = surface, surfaceContainerHigh = raised, surfaceContainerHighest = raised,
        surfaceVariant = raised, onSurfaceVariant = muted,
        inverseSurface = if (dark) Color(0xFFF5F5F5) else Color(0xFF181818),
        inverseOnSurface = if (dark) Color(0xFF202124) else Color(0xFFF0F0EE),
        outline = line, outlineVariant = line,
        error = danger, onError = onAccent,
        errorContainer = lerp(surface, danger, 0.09f), onErrorContainer = danger
    )
}
