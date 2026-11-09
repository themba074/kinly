package com.kinly.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OceanSkyColors = lightColorScheme(
    primary = Ocean700,
    onPrimary = Color.White,
    primaryContainer = Sky100,
    onPrimaryContainer = Ocean900,
    secondary = Sky500,
    onSecondary = Ocean900,
    background = Sky050,
    onBackground = Ink900,
    surface = Surface,
    onSurface = Ink900,
    surfaceVariant = Sky100,
    onSurfaceVariant = Ink600,
    outline = Border,
    error = Danger600,
    onError = Color.White,
    errorContainer = Danger100,
    onErrorContainer = Ink900,
)

@Composable
fun KinlyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OceanSkyColors,
        content = content,
    )
}
