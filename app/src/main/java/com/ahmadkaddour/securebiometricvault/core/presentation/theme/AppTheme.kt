package com.ahmadkaddour.securebiometricvault.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private object AppPalette {
    val Navy900 = Color(0xFF0B1F3A)
    val Navy800 = Color(0xFF132B4D)
    val Navy700 = Color(0xFF1C3A66)
    val Navy600 = Color(0xFF1E4D8C)
    val Navy400 = Color(0xFF3D72C4)
    val Navy200 = Color(0xFFADC8F0)
    val Navy100 = Color(0xFFD6E4F8)
    val Navy50 = Color(0xFFEDF3FC)

    val Gold500 = Color(0xFFD4A017)
    val Gold400 = Color(0xFFE5B432)
    val Gold100 = Color(0xFFFAF0D0)

    val Success500 = Color(0xFF1B8A5A)
    val Success100 = Color(0xFFD4EFDF)
    val Error500 = Color(0xFFCC2B2B)
    val Error100 = Color(0xFFF9DADA)

    val Neutral900 = Color(0xFF0F1117)
    val Neutral800 = Color(0xFF1C2130)
    val Neutral600 = Color(0xFF4A5568)
    val Neutral400 = Color(0xFF718096)
    val Neutral200 = Color(0xFFE2E8F0)
    val Neutral100 = Color(0xFFF7F8FA)
    val White = Color(0xFFFFFFFF)

    val Surface800 = Color(0xFF141B2D)
    val Surface700 = Color(0xFF1A2238)
    val Surface600 = Color(0xFF202A44)
}

internal val AppLightColorScheme = lightColorScheme(
    primary = AppPalette.Navy700,
    onPrimary = AppPalette.White,
    primaryContainer = AppPalette.Navy100,
    onPrimaryContainer = AppPalette.Navy900,

    secondary = AppPalette.Gold500,
    onSecondary = AppPalette.White,
    secondaryContainer = AppPalette.Gold100,
    onSecondaryContainer = AppPalette.Navy900,

    background = AppPalette.Neutral100,
    onBackground = AppPalette.Neutral900,

    surface = AppPalette.White,
    onSurface = AppPalette.Neutral900,
    surfaceVariant = AppPalette.Navy50,
    onSurfaceVariant = AppPalette.Neutral600,

    outline = AppPalette.Neutral200,

    error = AppPalette.Error500,
    onError = AppPalette.White,
    errorContainer = AppPalette.Error100,
    onErrorContainer = AppPalette.Error500,
)

internal val AppDarkColorScheme = darkColorScheme(
    primary = AppPalette.Navy400,
    onPrimary = AppPalette.Navy900,
    primaryContainer = AppPalette.Navy800,
    onPrimaryContainer = AppPalette.Navy200,

    secondary = AppPalette.Gold400,
    onSecondary = AppPalette.Navy900,
    secondaryContainer = AppPalette.Navy800,
    onSecondaryContainer = AppPalette.Gold100,

    background = AppPalette.Neutral900,
    onBackground = AppPalette.Neutral200,

    surface = AppPalette.Surface800,
    onSurface = AppPalette.Neutral200,
    surfaceVariant = AppPalette.Surface700,
    onSurfaceVariant = AppPalette.Neutral400,

    outline = AppPalette.Surface600,

    error = AppPalette.Error500,
    onError = AppPalette.White,
    errorContainer = Color(0xFF5C1A1A),
    onErrorContainer = AppPalette.Error100,
)

val AppTypography = Typography(
    displayLarge = TextStyle(fontSize = 57.sp, fontWeight = FontWeight.Light, lineHeight = 64.sp),
    displayMedium = TextStyle(fontSize = 45.sp, fontWeight = FontWeight.Light, lineHeight = 52.sp),
    headlineLarge = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Medium, lineHeight = 28.sp),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
)

val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) AppDarkColorScheme else AppLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
