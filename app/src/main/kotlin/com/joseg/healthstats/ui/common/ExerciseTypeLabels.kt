package com.joseg.healthstats.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Rowing
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.health.connect.client.records.ExerciseSessionRecord

/** Icon + accent color used to represent an exercise type as a color-coded badge. */
data class ExerciseTypeStyle(val icon: ImageVector, val color: Color)

private val RunningColor = Color(0xFFE0653D)
private val BikingColor = Color(0xFF3D7BE0)
private val WalkingColor = Color(0xFF4CAF6D)
private val SwimmingColor = Color(0xFF1FA6A0)
private val StrengthColor = Color(0xFF8355D6)
private val YogaColor = Color(0xFFB187E8)
private val OtherColor = Color(0xFF78909C)

/** Maps an exercise type to the icon/color used to render it as a compact colored badge. */
fun exerciseTypeStyle(type: Int): ExerciseTypeStyle = when (type) {
    ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> ExerciseTypeStyle(Icons.Filled.DirectionsRun, RunningColor)
    ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> ExerciseTypeStyle(Icons.Filled.DirectionsBike, BikingColor)
    ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> ExerciseTypeStyle(Icons.Filled.DirectionsWalk, WalkingColor)
    ExerciseSessionRecord.EXERCISE_TYPE_HIKING -> ExerciseTypeStyle(Icons.Filled.Hiking, WalkingColor)
    ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL -> ExerciseTypeStyle(Icons.Filled.Pool, SwimmingColor)
    ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER -> ExerciseTypeStyle(Icons.Filled.Pool, SwimmingColor)
    ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING -> ExerciseTypeStyle(Icons.Filled.FitnessCenter, StrengthColor)
    ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> ExerciseTypeStyle(Icons.Filled.SelfImprovement, YogaColor)
    ExerciseSessionRecord.EXERCISE_TYPE_ROWING -> ExerciseTypeStyle(Icons.Filled.Rowing, SwimmingColor)
    ExerciseSessionRecord.EXERCISE_TYPE_ELLIPTICAL -> ExerciseTypeStyle(Icons.Filled.FitnessCenter, OtherColor)
    ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT -> ExerciseTypeStyle(Icons.Filled.FitnessCenter, OtherColor)
    else -> ExerciseTypeStyle(Icons.Filled.FitnessCenter, OtherColor)
}

/** Human-readable label for the common exercise types; falls back to the raw type otherwise. */
fun exerciseTypeLabel(type: Int): String = when (type) {
    ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> "Running"
    ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> "Biking"
    ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> "Walking"
    ExerciseSessionRecord.EXERCISE_TYPE_HIKING -> "Hiking"
    ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL -> "Swimming (pool)"
    ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER -> "Swimming (open water)"
    ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING -> "Strength training"
    ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> "Yoga"
    ExerciseSessionRecord.EXERCISE_TYPE_ROWING -> "Rowing"
    ExerciseSessionRecord.EXERCISE_TYPE_ELLIPTICAL -> "Elliptical"
    ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT -> "Other workout"
    else -> "Exercise (type $type)"
}

/** Human-readable label for a source app's package name; falls back to the package name itself. */
fun sourceAppLabel(packageName: String): String = when (packageName) {
    "com.strava" -> "Strava"
    "com.garmin.android.apps.connectmobile" -> "Garmin Connect"
    "com.fitbit.FitbitMobile" -> "Fitbit"
    else -> packageName
}
