package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.WallpaperBackground
import com.example.ui.theme.AccentVioletPrimary

@Composable
fun SettingsScreen(
    wallpaperSettings: WallpaperSettings,
    onUpdateWallpaperSettings: (WallpaperSettings) -> Unit,
    onRescanLibrary: () -> Unit,
    onExportBackup: (onResult: (String) -> Unit) -> Unit,
    wifiOnlyDownloads: Boolean = false,
    autoDownloadFavorites: Boolean = false,
    streamingQuality: String = "320kbps High Quality",
    downloadQuality: String = "320kbps High Quality",
    onToggleWifiOnlyDownloads: (Boolean) -> Unit = {},
    onToggleAutoDownloadFavorites: (Boolean) -> Unit = {},
    onSelectStreamingQuality: (String) -> Unit = {},
    onSelectDownloadQuality: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var backupJsonDialog by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdateWallpaperSettings(
                wallpaperSettings.copy(
                    type = WallpaperType.GALLERY_URI,
                    customImageUri = uri.toString()
                )
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title Header
        item {
            Text(
                text = "Settings & Appearance",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Customize your wallpapers, dark aesthetic, and storage",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // --- WALLPAPER STUDIO SECTION ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = AccentVioletPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Wallpaper Studio",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TextButton(
                            onClick = {
                                onUpdateWallpaperSettings(
                                    WallpaperSettings(
                                        type = WallpaperType.PRESET,
                                        selectedPresetId = "preset_nebula",
                                        customImageUri = null,
                                        blurRadiusDp = 20f,
                                        brightness = 0.85f,
                                        opacity = 0.50f,
                                        overlayMode = OverlayMode.AUTO,
                                        overlayAlpha = 0.35f,
                                        position = WallpaperPosition.CENTER,
                                        scope = WallpaperScope.APP_WIDE,
                                        useDynamicColors = true,
                                        themeMode = AppThemeMode.DARK
                                    )
                                )
                            }
                        ) {
                            Text("Reset", color = AccentVioletPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Studio Mini Preview Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    ) {
                        WallpaperBackground(settings = wallpaperSettings)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Black.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = if (wallpaperSettings.type == WallpaperType.GALLERY_URI) "Custom Photo Wallpaper" else WallpaperPresets.getById(wallpaperSettings.selectedPresetId).name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Preset Palettes Carousel
                    Text(
                        text = "Curated Presets",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WallpaperPresets.presets.forEach { preset ->
                            val isSelected = wallpaperSettings.type == WallpaperType.PRESET && wallpaperSettings.selectedPresetId == preset.id

                            Box(
                                modifier = Modifier
                                    .width(110.dp)
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(preset.gradientStartColor),
                                                Color(preset.gradientEndColor)
                                            )
                                        )
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) AccentVioletPrimary else Color.White.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        onUpdateWallpaperSettings(
                                            wallpaperSettings.copy(
                                                type = WallpaperType.PRESET,
                                                selectedPresetId = preset.id
                                            )
                                        )
                                    }
                                    .padding(8.dp),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Text(
                                    text = preset.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gallery Photo Picker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pick_custom_wallpaper_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentVioletPrimary,
                                contentColor = Color(0xFF381E72)
                            )
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pick from Gallery", fontWeight = FontWeight.Bold)
                        }

                        if (wallpaperSettings.type == WallpaperType.GALLERY_URI) {
                            OutlinedButton(
                                onClick = {
                                    onUpdateWallpaperSettings(
                                        wallpaperSettings.copy(
                                            type = WallpaperType.PRESET,
                                            customImageUri = null
                                        )
                                    )
                                }
                            ) {
                                Text("Clear")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Blur Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Blur Strength", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${wallpaperSettings.blurRadiusDp.toInt()} dp",
                                style = MaterialTheme.typography.labelMedium,
                                color = AccentVioletPrimary
                            )
                        }
                        Slider(
                            value = wallpaperSettings.blurRadiusDp,
                            onValueChange = { onUpdateWallpaperSettings(wallpaperSettings.copy(blurRadiusDp = it)) },
                            valueRange = 0f..40f,
                            colors = SliderDefaults.colors(
                                thumbColor = AccentVioletPrimary,
                                activeTrackColor = AccentVioletPrimary
                            ),
                            modifier = Modifier.testTag("wallpaper_blur_slider")
                        )
                    }

                    // Brightness Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Brightness", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${(wallpaperSettings.brightness * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = AccentVioletPrimary
                            )
                        }
                        Slider(
                            value = wallpaperSettings.brightness,
                            onValueChange = { onUpdateWallpaperSettings(wallpaperSettings.copy(brightness = it)) },
                            valueRange = 0.2f..1.2f,
                            colors = SliderDefaults.colors(
                                thumbColor = AccentVioletPrimary,
                                activeTrackColor = AccentVioletPrimary
                            ),
                            modifier = Modifier.testTag("wallpaper_brightness_slider")
                        )
                    }

                    // Opacity Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Opacity", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${(wallpaperSettings.opacity * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = AccentVioletPrimary
                            )
                        }
                        Slider(
                            value = wallpaperSettings.opacity,
                            onValueChange = { onUpdateWallpaperSettings(wallpaperSettings.copy(opacity = it)) },
                            valueRange = 0.1f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = AccentVioletPrimary,
                                activeTrackColor = AccentVioletPrimary
                            ),
                            modifier = Modifier.testTag("wallpaper_opacity_slider")
                        )
                    }

                    // Overlay Mode Selector
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Contrast Overlay Mode", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OverlayMode.entries.forEach { mode ->
                            val isSelected = wallpaperSettings.overlayMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { onUpdateWallpaperSettings(wallpaperSettings.copy(overlayMode = mode)) },
                                label = { Text(mode.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentVioletPrimary,
                                    selectedLabelColor = Color(0xFF381E72)
                                )
                            )
                        }
                    }

                    // Wallpaper Scope
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Wallpaper Scope", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WallpaperScope.entries.forEach { scope ->
                            val isSelected = wallpaperSettings.scope == scope
                            FilterChip(
                                selected = isSelected,
                                onClick = { onUpdateWallpaperSettings(wallpaperSettings.copy(scope = scope)) },
                                label = { Text(if (scope == WallpaperScope.APP_WIDE) "App-wide" else "Now Playing Only") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentVioletPrimary,
                                    selectedLabelColor = Color(0xFF381E72)
                                )
                            )
                        }
                    }
                }
            }
        }

        // --- THEME & APPEARANCE ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Theme & Display",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppThemeMode.entries.forEach { mode ->
                            val isSelected = wallpaperSettings.themeMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { onUpdateWallpaperSettings(wallpaperSettings.copy(themeMode = mode)) },
                                label = { Text(mode.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentVioletPrimary,
                                    selectedLabelColor = Color(0xFF381E72)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Dynamic Palette Theming",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Derive UI accent tokens dynamically from active wallpaper",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = wallpaperSettings.useDynamicColors,
                            onCheckedChange = { onUpdateWallpaperSettings(wallpaperSettings.copy(useDynamicColors = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF381E72),
                                checkedTrackColor = AccentVioletPrimary
                            )
                        )
                    }
                }
            }
        }

        // --- STORAGE & BACKUP ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Library & Storage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onRescanLibrary() }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = AccentVioletPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Rescan Device Media", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Query MediaStore for newly added music",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onExportBackup { json ->
                                    backupJsonDialog = json
                                }
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = null, tint = AccentVioletPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Backup Playlists & Settings", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Export offline configuration to JSON",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // --- DOWNLOADS & ONLINE STREAMING CONFIGURATION ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = AccentVioletPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Downloads & Online Streaming",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Wi-Fi Only Downloads
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Download over Wi-Fi Only", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Prevents accidental cellular data usage",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = wifiOnlyDownloads,
                            onCheckedChange = onToggleWifiOnlyDownloads,
                            colors = SwitchDefaults.colors(checkedThumbColor = AccentVioletPrimary)
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

                    // Auto-Download Favorites
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Auto-Download Favorites", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Save authorized favorite songs for offline use",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = autoDownloadFavorites,
                            onCheckedChange = onToggleAutoDownloadFavorites,
                            colors = SwitchDefaults.colors(checkedThumbColor = AccentVioletPrimary)
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

                    // Streaming Quality Selection
                    Text(text = "Streaming Quality", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("192kbps AAC", "320kbps High Quality", "FLAC Lossless").forEach { q ->
                            val isSelected = streamingQuality == q
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) AccentVioletPrimary.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.04f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AccentVioletPrimary else Color.Transparent),
                                modifier = Modifier
                                    .clickable { onSelectStreamingQuality(q) }
                                    .weight(1f)
                            ) {
                                Text(
                                    text = q.replace(" Quality", ""),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AccentVioletPrimary else Color.White.copy(alpha = 0.7f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

                    // Storage Folder Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Download Storage Folder", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                            Text(text = "Music/Novatune/", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }
        }

        // --- ABOUT, LEGAL & PRIVACY ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "About Novatune & Legal Compliance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Novatune provides privacy-respecting local playback and authorized online music streaming. Online music is strictly sourced from authorized catalogs, Creative Commons (CC BY 4.0 / CC0), and Public Domain archives with full artist metadata and licenses. We strictly prohibit scraping or unauthorized downloading.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF81D4FA), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Licensed & Public Domain Adherence Verified",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF81D4FA),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Version 1.1.0 • Online & Offline Vault Edition",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentVioletPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Export Backup JSON Dialog
    if (backupJsonDialog != null) {
        AlertDialog(
            onDismissRequest = { backupJsonDialog = null },
            title = { Text("Exported Backup") },
            text = {
                Column {
                    Text(
                        text = "Your local playlists and wallpaper configurations have been exported:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                    ) {
                        Text(
                            text = backupJsonDialog ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Novatune Backup", backupJsonDialog)
                        clipboard.setPrimaryClip(clip)
                        backupJsonDialog = null
                    }
                ) {
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { backupJsonDialog = null }) {
                    Text("Close")
                }
            }
        )
    }
}
