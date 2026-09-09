package com.joseg.healthstats.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Brand palette: a calm-but-energetic teal/emerald primary (trust + vitality for a health-data
// app) with a warm coral tertiary reserved for heart-rate/calorie accents.
val Emerald40 = Color(0xFF00695C)
val Emerald80 = Color(0xFF4FD8C4)
val Emerald90 = Color(0xFFB6F2E6)
val Emerald10 = Color(0xFF00201C)

val EmeraldContainerLight = Color(0xFF8FF0DE)
val EmeraldContainerDark = Color(0xFF00504A)

val Slate40 = Color(0xFF4A6360)
val Slate80 = Color(0xFFB1CCC7)
val SlateContainerLight = Color(0xFFCDE9E3)
val SlateContainerDark = Color(0xFF334B48)

val Coral40 = Color(0xFFB3492E)
val Coral80 = Color(0xFFFFB4A0)
val CoralContainerLight = Color(0xFFFFDBD0)
val CoralContainerDark = Color(0xFF8C3319)

val Error40 = Color(0xFFBA1A1A)
val Error80 = Color(0xFFFFB4AB)
val ErrorContainerLight = Color(0xFFFFDAD6)
val ErrorContainerDark = Color(0xFF93000A)

val BackgroundLight = Color(0xFFF6FBF9)
val BackgroundDark = Color(0xFF0E1513)
val SurfaceLight = Color(0xFFF6FBF9)
val SurfaceDark = Color(0xFF0E1513)
val OnSurfaceLight = Color(0xFF161D1B)
val OnSurfaceDark = Color(0xFFE0E3E1)
val SurfaceVariantLight = Color(0xFFDAE5E1)
val SurfaceVariantDark = Color(0xFF3F4947)
val OutlineLight = Color(0xFF6F7976)
val OutlineDark = Color(0xFF899390)

val HealthStatsLightColorScheme = lightColorScheme(
    primary = Emerald40,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainerLight,
    onPrimaryContainer = Emerald10,
    secondary = Slate40,
    onSecondary = Color.White,
    secondaryContainer = SlateContainerLight,
    onSecondaryContainer = Color(0xFF06201D),
    tertiary = Coral40,
    onTertiary = Color.White,
    tertiaryContainer = CoralContainerLight,
    onTertiaryContainer = Color(0xFF3B0900),
    error = Error40,
    onError = Color.White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = Color(0xFF410002),
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF3F4947),
    outline = OutlineLight,
)

val HealthStatsDarkColorScheme = darkColorScheme(
    primary = Emerald80,
    onPrimary = Color(0xFF003731),
    primaryContainer = EmeraldContainerDark,
    onPrimaryContainer = Emerald90,
    secondary = Slate80,
    onSecondary = Color(0xFF1C3532),
    secondaryContainer = SlateContainerDark,
    onSecondaryContainer = Color(0xFFCDE9E3),
    tertiary = Coral80,
    onTertiary = Color(0xFF5C1900),
    tertiaryContainer = CoralContainerDark,
    onTertiaryContainer = Color(0xFFFFDBD0),
    error = Error80,
    onError = Color(0xFF690005),
    errorContainer = ErrorContainerDark,
    onErrorContainer = ErrorContainerLight,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFBEC9C6),
    outline = OutlineDark,
)
