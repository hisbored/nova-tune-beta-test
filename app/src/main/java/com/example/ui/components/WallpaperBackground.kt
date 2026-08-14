package com.example.ui.components

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.theme.ObsidianBackground

@Composable
fun WallpaperBackground(
    settings: WallpaperSettings,
    modifier: Modifier = Modifier
) {
    val preset = WallpaperPresets.getById(settings.selectedPresetId)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        // Wallpaper visual layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(settings.opacity.coerceIn(0.05f, 1f))
        ) {
            when {
                settings.type == WallpaperType.GALLERY_URI && !settings.customImageUri.isNullOrEmpty() -> {
                    val colorMatrix = ColorMatrix().apply {
                        setToScale(
                            settings.brightness,
                            settings.brightness,
                            settings.brightness,
                            1f
                        )
                    }

                    val alignment = when (settings.position) {
                        WallpaperPosition.TOP -> Alignment.TopCenter
                        WallpaperPosition.CENTER -> Alignment.Center
                        WallpaperPosition.BOTTOM -> Alignment.BottomCenter
                    }

                    AsyncImage(
                        model = Uri.parse(settings.customImageUri),
                        contentDescription = "Custom Wallpaper",
                        contentScale = ContentScale.Crop,
                        alignment = alignment,
                        colorFilter = ColorFilter.colorMatrix(colorMatrix),
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (settings.blurRadiusDp > 0f) {
                                    Modifier.blur(settings.blurRadiusDp.dp)
                                } else Modifier
                            )
                    )
                }
                else -> {
                    // Ambient radial & linear gradient matching the Elegant Dark preset
                    val startColor = Color(preset.gradientStartColor)
                    val endColor = Color(preset.gradientEndColor)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(startColor, Color.Transparent),
                                    center = Offset(200f, 300f),
                                    radius = 1200f
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(endColor, Color.Transparent),
                                    center = Offset(800f, 1800f),
                                    radius = 1400f
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        startColor.copy(alpha = 0.4f * settings.brightness),
                                        Color.Transparent,
                                        endColor.copy(alpha = 0.5f * settings.brightness)
                                    )
                                )
                            )
                            .then(
                                if (settings.blurRadiusDp > 0f) {
                                    Modifier.blur(settings.blurRadiusDp.dp)
                                } else Modifier
                            )
                    )
                }
            }
        }

        // Intelligent Readability Scrim / Contrast Overlay
        val overlayColor = when (settings.overlayMode) {
            OverlayMode.DARK -> Color.Black
            OverlayMode.LIGHT -> Color.White
            OverlayMode.AUTO -> Color.Black
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            overlayColor.copy(alpha = (settings.overlayAlpha + 0.25f).coerceIn(0f, 0.95f)),
                            overlayColor.copy(alpha = settings.overlayAlpha.coerceIn(0f, 0.9f)),
                            overlayColor.copy(alpha = (settings.overlayAlpha + 0.35f).coerceIn(0f, 0.98f))
                        )
                    )
                )
        )
    }
}
