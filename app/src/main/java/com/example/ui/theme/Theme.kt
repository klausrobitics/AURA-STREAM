package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeMode {
    DARK_OBSIDIAN,
    AMOLED_BLACK,
    LIGHT,
    SYSTEM
}

private val ObsidianColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF041E26),
    primaryContainer = Color(0xFF003846),
    onPrimaryContainer = Color(0xFFA6EEFF),
    secondary = ElectricViolet,
    onSecondary = Color.White,
    secondaryContainer = DeepViolet,
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = NeonEmerald,
    onTertiary = Color.Black,
    background = ObsidianBackground,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = ObsidianCardBorder
)

private val AmoledColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF0F1E24),
    onPrimaryContainer = NeonCyan,
    secondary = ElectricViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E1430),
    onSecondaryContainer = ElectricViolet,
    tertiary = NeonEmerald,
    onTertiary = Color.Black,
    background = AmoledBackground,
    onBackground = Color.White,
    surface = AmoledSurface,
    onSurface = Color.White,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = AmoledCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007A8D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC7F3FB),
    onPrimaryContainer = Color(0xFF002026),
    secondary = ElectricViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF3B0764),
    tertiary = Color(0xFF059669),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightCardBorder
)

@Composable
fun AuraStreamTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK_OBSIDIAN,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        AppThemeMode.DARK_OBSIDIAN -> ObsidianColorScheme
        AppThemeMode.AMOLED_BLACK -> AmoledColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.SYSTEM -> if (isSystemDark) ObsidianColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
