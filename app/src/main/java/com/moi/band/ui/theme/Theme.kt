package com.moi.band.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== POWER METAL THEME (Epic Golden) ====================
private val PowerMetalDarkColors = darkColorScheme(
    primary = Color(0xFFFFD700),        // Золотой
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = Color(0xFFB8860B), // Темное золото
    onPrimaryContainer = Color(0xFFFFFFFF),
    
    secondary = Color(0xFFFFA500),      // Оранжевый
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = Color(0xFFFF8C00),
    onSecondaryContainer = Color(0xFFFFFFFF),
    
    tertiary = Color(0xFFFF4500),       // Красно-оранжевый
    onTertiary = Color(0xFFFFFFFF),
    
    background = Color(0xFF0F0F0F),     // Почти черный
    onBackground = Color(0xFFE0E0E0),
    
    surface = Color(0xFF1A1A1A),        // Темно-серый
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFC0C0C0),
    
    error = Color(0xFFCF6679),
    onError = Color(0xFF000000),
    
    outline = Color(0xFF8B7355),        // Коричневато-золотой
    outlineVariant = Color(0xFF4A4A4A)
)

// ==================== HEAVY METAL THEME (Classic Steel) ====================
private val HeavyMetalDarkColors = darkColorScheme(
    primary = Color(0xFFC0C0C0),        // Серебристый
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF696969), // Тускло-серый
    onPrimaryContainer = Color(0xFFFFFFFF),
    
    secondary = Color(0xFF708090),      // Slate Gray
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF4682B4), // Steel Blue
    onSecondaryContainer = Color(0xFFFFFFFF),
    
    tertiary = Color(0xFF8B0000),       // Dark Red
    onTertiary = Color(0xFFFFFFFF),
    
    background = Color(0xFF121212),     // Очень темный
    onBackground = Color(0xFFD3D3D3),
    
    surface = Color(0xFF1C1C1C),        // Темный
    onSurface = Color(0xFFD3D3D3),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),
    
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF),
    
    outline = Color(0xFF555555),
    outlineVariant = Color(0xFF3A3A3A)
)

// ==================== PUNK ROCK THEME (Neon Chaos) ====================
private val PunkRockDarkColors = darkColorScheme(
    primary = Color(0xFFFF1493),        // Deep Pink
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFFDC143C), // Crimson
    onPrimaryContainer = Color(0xFFFFFFFF),
    
    secondary = Color(0xFF00FF00),      // Lime Green
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF32CD32), // Lime Green
    onSecondaryContainer = Color(0xFF000000),
    
    tertiary = Color(0xFF00CED1),       // Dark Turquoise
    onTertiary = Color(0xFF000000),
    
    background = Color(0xFF0A0A0A),     // Почти черный
    onBackground = Color(0xFFFFFFFF),
    
    surface = Color(0xFF151515),        // Очень темный
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF202020),
    onSurfaceVariant = Color(0xFFE0E0E0),
    
    error = Color(0xFFFF0000),          // Чистый красный
    onError = Color(0xFFFFFFFF),
    
    outline = Color(0xFFFF1493),        // Pink outline
    outlineVariant = Color(0xFF444444)
)

// ==================== LIGHT THEMES (для справки, не используем) ====================
private val PowerMetalLightColors = lightColorScheme(
    primary = Color(0xFFB8860B),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFFF8C00),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFFFAF0),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFF8DC),
    onSurface = Color(0xFF1A1A1A)
)

/**
 * 🎨 MoiBandTheme - главная тема приложения
 * 
 * @param theme Название темы: "dark" (Power Metal), "light" (Heavy Metal), "punk" (Punk Rock)
 * @param content Composable контент
 */
@Composable
fun MoiBandTheme(
    theme: String = "dark",
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        "light" -> HeavyMetalDarkColors  // Heavy Metal (классический строгий)
        "punk" -> PunkRockDarkColors     // Punk Rock (яркий неоновый)
        else -> PowerMetalDarkColors     // Power Metal (золотой эпик) - по умолчанию
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
