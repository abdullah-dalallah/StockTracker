// In ui/theme/Theme.kt

package com.multibank.stocktracker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- Custom Colors Definition ---
data class CustomColors(
    val priceIncrease: Color,
    val priceDecrease: Color
)

val LocalCustomColors = compositionLocalOf {
    CustomColors(
        priceIncrease = Color.Unspecified,
        priceDecrease = Color.Unspecified
    )
}
// --- End Custom Colors Definition ---

private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    background = DarkGrey,
    surface = DarkGrey,
    onPrimary = Color.Black,
    onBackground = OffWhite,
    onSurface = OffWhite
)

private val LightColorScheme = lightColorScheme(
    primary = DarkBlue,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun StockTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val customColors = CustomColors(
        priceIncrease = PriceGreen,
        priceDecrease = PriceRed
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // VVV THIS IS THE FIX VVV
    // We wrap the whole theme in a CompositionLocalProvider to make our
    // custom colors available to all composables inside.
    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// --- Custom Theme Accessor ---
val MaterialTheme.customColors: CustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current
// --- End Custom Theme Accessor ---