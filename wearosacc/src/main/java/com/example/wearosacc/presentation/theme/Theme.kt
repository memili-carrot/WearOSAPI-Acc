package com.example.wearosacc.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.Typography

private val WearColorPalette = Colors(
    primary = androidx.compose.ui.graphics.Color(0xFF4285F4), // Google Blue
    onPrimary = androidx.compose.ui.graphics.Color.White,
    background = androidx.compose.ui.graphics.Color.Black,
    onBackground = androidx.compose.ui.graphics.Color.White
)

@Composable
fun WearOSGPSTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = WearColorPalette,
        typography = Typography(),
        content = content
    )
}