package com.joseg.healthstats.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Circular color-coded icon badge for an exercise type: a soft tint of the type's accent color
 * behind a solid icon in that same color, reused small (list rows) and large (detail hero).
 */
@Composable
fun ExerciseTypeBadge(
    exerciseType: Int,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    val style = exerciseTypeStyle(exerciseType)
    Box(
        modifier = modifier
            .size(size)
            .background(color = style.color.copy(alpha = 0.14f), shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = style.icon,
            contentDescription = null,
            tint = style.color,
            modifier = Modifier.size(size * 0.55f),
        )
    }
}
