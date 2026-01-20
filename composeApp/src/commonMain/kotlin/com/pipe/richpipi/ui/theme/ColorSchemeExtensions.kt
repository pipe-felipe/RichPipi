package com.pipe.richpipi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * Semantic color aliases for RichPipiTheme.
 *
 * Use these from UI code via `MaterialTheme.colorScheme.<name>` so they automatically
 * follow the active light/dark scheme.
 */
val ColorScheme.golden
    get() = surfaceBright

val ColorScheme.money
    get() = surfaceDim

val ColorScheme.incomeBackground
    @Composable get() =
        if (isSystemInDarkTheme())
            DarkContainerIncomeBackground
        else
            LightContainerIncomeBackground

val ColorScheme.expenseBackground
    @Composable get() =
        if (isSystemInDarkTheme())
            DarkContainerExpenseBackground
        else LightContainerExpenseBackground
