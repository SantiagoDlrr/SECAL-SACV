package com.secal.juraid.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Colores compartidos
val Primary = Color(0xFF1F3349)  // Azul oscuro como acento
val Secondary = Color(0xFF909395)  // Gris medio
val Tertiary = Color(0xFFABB5B5)  // Gris claro
val Error = Color(0xFFB00020)


// Colores para el tema claro
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFFFFFFF)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightOnSecondary = Color(0xFF000000)
val LightOnTertiary = Color(0xFF000000)
val LightOnBackground = Color(0xFF000000)
val LightOnSurface = Color(0xFF000000)
val LightOnError = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFE1E3E3)
val LightOnSecondaryContainer = Color(0xFF303030)
val LightPrimaryContainer = Color(0xFFD6E3F0)
val LightOnPrimaryContainer = Color(0xFF1F3349)
val LightGreen = Color(0xFF3B833E)
val LightRed = Color(0xFFE53935)

// Colores específicos para el tema oscuro
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkOnPrimary = Color(0xFFE8F0F8)
val DarkOnSecondary = Color(0xFFE1E3E3)
val DarkOnTertiary = Color(0xFFE1E3E3)
val DarkOnBackground = Color(0xFFE1E3E3)
val DarkOnSurface = Color(0xFFE1E3E3)
val DarkOnError = Color(0xFF121212)
val DarkSecondaryContainer = Color(0xFF424242)
val DarkOnSecondaryContainer = Color(0xFFE1E3E3)
val DarkPrimaryContainer = Color(0xFF2C4A6B)
val DarkOnPrimaryContainer = Color(0xFFD6E3F0)
val DarkGreen = Color(0xFF4CAF50)
val DarkRed = Color(0xFFE53935)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,
    background = DarkBackground,
    surface = DarkSurface,
    error = Error,
    onPrimary = DarkOnPrimary,
    onSecondary = DarkOnSecondary,
    onTertiary = DarkOnTertiary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onError = DarkOnError,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,
    background = LightBackground,
    surface = LightSurface,
    error = Error,
    onPrimary = LightOnPrimary,
    onSecondary = LightOnSecondary,
    onTertiary = LightOnTertiary,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    onError = LightOnError,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer
)

@Composable
fun JurAidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}