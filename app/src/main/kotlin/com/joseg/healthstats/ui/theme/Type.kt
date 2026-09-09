package com.joseg.healthstats.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Kept to Material 3's default type scale (system font, no new asset) but only 4 roles are used
// deliberately across the app: headline (screen titles), title (section headers), body (content),
// label (captions/metadata) — see the mobile-app-ui-design skill's "max 4 font sizes" guidance.
val HealthStatsTypography = Typography()

/** Large numeric stat values (session detail) use tabular monospace figures for readability. */
val StatValueTextStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 34.sp,
)
