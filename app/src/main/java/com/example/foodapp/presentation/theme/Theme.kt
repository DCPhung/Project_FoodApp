package com.example.foodapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LightLavender,
    secondary = MediumPurple,
    tertiary = White,
    background = Color(0xFF1A0F20),
    surface = Color(0xFF25182B),
    onPrimary = DeepBlack,
    onSecondary = DeepBlack,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = MediumPurple,
    secondary = LightLavender,
    tertiary = DeepBlack,
    background = Color(0xFF4B3F72),
    surface = LightLavender,
    onPrimary = Color(0xFF4B3F72),
    onSecondary = DeepBlack,
    onBackground = White,
    onSurface = DeepBlack
)

@Composable
fun FoodAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
