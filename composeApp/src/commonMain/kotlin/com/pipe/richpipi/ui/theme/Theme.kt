package com.pipe.richpipi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkPiPrimary,
    secondary = DarkPiSecondary,
    tertiary = DarkPiTertiary,
    surfaceBright = PiGolden,
    surfaceDim = DarkPiMoney
)

private val LightColorScheme = lightColorScheme(
    primary = LightPiPrimary,
    secondary = LightPiSecondary,
    tertiary = LightPiTertiary,
    surfaceBright = PiGolden,
    surfaceDim = LightPiMoney
)

@Composable
fun RichPipiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
