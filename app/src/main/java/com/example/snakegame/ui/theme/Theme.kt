package com.example.snakegame.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF8F00),
    secondary = Color(0xFFFFB74D),
    background = Color(0xFF2E1A0E),
    surface = Color(0xFF3E2723),
    onPrimary = Color.White,
    onBackground = Color(0xFFFFF8E1),
    onSurface = Color(0xFFFFF8E1)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE65100),
    secondary = Color(0xFFFF8F00),
    background = Color(0xFFFFF8E1),
    surface = Color(0xFFFFECB3),
    onPrimary = Color.White,
    onBackground = Color(0xFF3E2723),
    onSurface = Color(0xFF3E2723)
)

@Composable
fun SnakeGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
