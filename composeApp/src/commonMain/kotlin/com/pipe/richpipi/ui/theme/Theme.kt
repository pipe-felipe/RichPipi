package com.pipe.richpipi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkPiPrimary,
    onPrimary = DarkPiOnPrimary,
    primaryContainer = DarkPiPrimaryContainer,
    onPrimaryContainer = DarkPiOnPrimaryContainer,
    secondary = DarkPiSecondary,
    onSecondary = DarkPiOnSecondary,
    secondaryContainer = DarkPiSecondaryContainer,
    onSecondaryContainer = DarkPiOnSecondaryContainer,
    tertiary = DarkPiTertiary,
    onTertiary = DarkPiOnTertiary,
    tertiaryContainer = DarkPiTertiaryContainer,
    onTertiaryContainer = DarkPiOnTertiaryContainer,
    background = DarkPiBackground,
    onBackground = DarkPiOnBackground,
    surface = DarkPiSurface,
    onSurface = DarkPiOnSurface,
    surfaceVariant = DarkPiSurfaceVariant,
    onSurfaceVariant = DarkPiOnSurfaceVariant,
    outline = DarkPiOutline,
    surfaceBright = PiGolden,
    surfaceDim = DarkPiMoney,
)

private val LightColorScheme = lightColorScheme(
    primary = LightPiPrimary,
    onPrimary = LightPiOnPrimary,
    primaryContainer = LightPiPrimaryContainer,
    onPrimaryContainer = LightPiOnPrimaryContainer,
    secondary = LightPiSecondary,
    onSecondary = LightPiOnSecondary,
    secondaryContainer = LightPiSecondaryContainer,
    onSecondaryContainer = LightPiOnSecondaryContainer,
    tertiary = LightPiTertiary,
    onTertiary = LightPiOnTertiary,
    tertiaryContainer = LightPiTertiaryContainer,
    onTertiaryContainer = LightPiOnTertiaryContainer,
    background = LightPiBackground,
    onBackground = LightPiOnBackground,
    surface = LightPiSurface,
    onSurface = LightPiOnSurface,
    surfaceVariant = LightPiSurfaceVariant,
    onSurfaceVariant = LightPiOnSurfaceVariant,
    outline = LightPiOutline,
    surfaceBright = PiGolden,
    surfaceDim = LightPiMoney,
)

@Composable
fun RichPipiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content,
    )
}
