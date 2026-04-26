package com.pharmacare.inventory.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Dark color scheme for the PharmaCare app.
 *
 * Material3 dark schemes use the "on*" colors for text/icons placed ON
 * the corresponding container color (e.g., [onPrimary] text sits on
 * [primary] backgrounds).
 */
private val DarkColorScheme = darkColorScheme(
    primary          = PrimaryGreen,
    onPrimary        = OnPrimaryWhite,
    primaryContainer = PrimaryGreenDark,
    onPrimaryContainer = PrimaryGreenLight,

    secondary        = SecondaryAmber,
    onSecondary      = Color(0xFF1A1A00),
    secondaryContainer = Color(0xFF4A3800),
    onSecondaryContainer = SecondaryAmberLight,

    background       = BackgroundDark,
    onBackground     = OnSurfaceWhite,

    surface          = SurfaceDark,
    onSurface        = OnSurfaceWhite,
    surfaceVariant   = SurfaceVariant,
    onSurfaceVariant = OnSurfaceSecondary,

    error            = ErrorRed,
    onError          = OnPrimaryWhite,

    outline          = Color(0xFF3D5163)
)

/**
 * Root Composable theme wrapper for the entire PharmaCare application.
 *
 * Wrap your top-level [androidx.compose.ui.window.Popup] or
 * [androidx.activity.compose.setContent] block with this to apply the
 * custom color scheme and typography throughout the entire UI tree.
 *
 * @param content The Composable content to display inside the theme.
 */
@Composable
fun PharmacareTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
