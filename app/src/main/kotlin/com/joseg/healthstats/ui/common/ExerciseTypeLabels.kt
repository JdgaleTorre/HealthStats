package com.joseg.healthstats.ui.common

import androidx.health.connect.client.records.ExerciseSessionRecord

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
