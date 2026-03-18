package com.family.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

// Ocean-sunset palette for a clean, premium, global-app look.
private val LightPalette: ColorScheme = lightColorScheme(
    primary = Color(0xFF0A7EA4),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD5F1F9),
    onPrimaryContainer = Color(0xFF003547),
    secondary = Color(0xFF2E93C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD9EEFB),
    onSecondaryContainer = Color(0xFF113B52),
    tertiary = Color(0xFF2D9F6F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD5F5E5),
    onTertiaryContainer = Color(0xFF00391E),
    error = Color(0xFFB42318),
    background = Color(0xFFF4FBFD),
    onBackground = Color(0xFF10252C),
    surface = Color(0xFFFCFEFF),
    onSurface = Color(0xFF10252C),
    surfaceVariant = Color(0xFFE0EEF4),
    onSurfaceVariant = Color(0xFF445E67),
    outline = Color(0xFF7B99A3),
    outlineVariant = Color(0xFFC8D9DF),
    surfaceContainer = Color(0xFFF0F8FB),
    surfaceContainerHigh = Color(0xFFE8F2F6),
    surfaceContainerHighest = Color(0xFFDDECF2)
)

private val DarkPalette: ColorScheme = darkColorScheme(
    primary = Color(0xFF7BD2EB),
    onPrimary = Color(0xFF003547),
    primaryContainer = Color(0xFF025672),
    onPrimaryContainer = Color(0xFFD5F1F9),
    secondary = Color(0xFFAEDBF5),
    onSecondary = Color(0xFF113B52),
    secondaryContainer = Color(0xFF234B63),
    onSecondaryContainer = Color(0xFFD9EEFB),
    tertiary = Color(0xFF94E3BC),
    onTertiary = Color(0xFF00391E),
    tertiaryContainer = Color(0xFF126B45),
    onTertiaryContainer = Color(0xFFD5F5E5),
    background = Color(0xFF08161B),
    onBackground = Color(0xFFDDECF2),
    surface = Color(0xFF0D1C22),
    onSurface = Color(0xFFDDECF2),
    surfaceVariant = Color(0xFF20343D),
    onSurfaceVariant = Color(0xFFAFC5CE),
    outline = Color(0xFF6B8791),
    outlineVariant = Color(0xFF2D4650),
    surfaceContainer = Color(0xFF13262D),
    surfaceContainerHigh = Color(0xFF1A2F37),
    surfaceContainerHighest = Color(0xFF213A44)
)

private val FamilyTypography = Typography(
    displaySmall = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 38.sp, lineHeight = 44.sp, letterSpacing = (-0.4).sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 33.sp, lineHeight = 39.sp, letterSpacing = (-0.2).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 29.sp, lineHeight = 35.sp),
    headlineSmall = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 21.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 17.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 22.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.2.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.3.sp)
)

private val FamilyShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun FamilyTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkPalette else LightPalette
    MaterialTheme(
        colorScheme = colors,
        typography = FamilyTypography,
        shapes = FamilyShapes,
        content = content
    )
}
