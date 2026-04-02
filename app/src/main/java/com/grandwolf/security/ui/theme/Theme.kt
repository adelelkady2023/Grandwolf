package com.grandwolf.security.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0E1A2A),
    secondary = Color(0xFFE2B462),
    tertiary = Color(0xFF4A90E2),
    background = Color(0xFFF2F5F9),
    surface = Color.White
)

@Composable
fun GrandWolfSecurityTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
