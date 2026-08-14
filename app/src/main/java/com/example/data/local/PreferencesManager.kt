package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("novatune_prefs", Context.MODE_PRIVATE)

    private val _wallpaperSettings = MutableStateFlow(loadWallpaperSettings())
    val wallpaperSettings: StateFlow<WallpaperSettings> = _wallpaperSettings.asStateFlow()

    private val _hasSeenOnboarding = MutableStateFlow(prefs.getBoolean(KEY_HAS_SEEN_ONBOARDING, false))
    val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding.asStateFlow()

    private val _sleepTimerMinutes = MutableStateFlow<Int?>(null)
    val sleepTimerMinutes: StateFlow<Int?> = _sleepTimerMinutes.asStateFlow()

    private fun loadWallpaperSettings(): WallpaperSettings {
        val typeStr = prefs.getString(KEY_TYPE, WallpaperType.PRESET.name) ?: WallpaperType.PRESET.name
        val type = runCatching { WallpaperType.valueOf(typeStr) }.getOrDefault(WallpaperType.PRESET)
        val presetId = prefs.getString(KEY_PRESET_ID, "preset_nebula") ?: "preset_nebula"
        val customUri = prefs.getString(KEY_CUSTOM_URI, null)
        val blur = prefs.getFloat(KEY_BLUR, 20f)
        val brightness = prefs.getFloat(KEY_BRIGHTNESS, 0.85f)
        val opacity = prefs.getFloat(KEY_OPACITY, 0.50f)
        val overlayModeStr = prefs.getString(KEY_OVERLAY_MODE, OverlayMode.AUTO.name) ?: OverlayMode.AUTO.name
        val overlayMode = runCatching { OverlayMode.valueOf(overlayModeStr) }.getOrDefault(OverlayMode.AUTO)
        val overlayAlpha = prefs.getFloat(KEY_OVERLAY_ALPHA, 0.35f)
        val posStr = prefs.getString(KEY_POSITION, WallpaperPosition.CENTER.name) ?: WallpaperPosition.CENTER.name
        val pos = runCatching { WallpaperPosition.valueOf(posStr) }.getOrDefault(WallpaperPosition.CENTER)
        val scopeStr = prefs.getString(KEY_SCOPE, WallpaperScope.APP_WIDE.name) ?: WallpaperScope.APP_WIDE.name
        val scope = runCatching { WallpaperScope.valueOf(scopeStr) }.getOrDefault(WallpaperScope.APP_WIDE)
        val dynamicColors = prefs.getBoolean(KEY_DYNAMIC_COLORS, true)
        val themeModeStr = prefs.getString(KEY_THEME_MODE, AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name
        val themeMode = runCatching { AppThemeMode.valueOf(themeModeStr) }.getOrDefault(AppThemeMode.DARK)

        return WallpaperSettings(
            type = type,
            selectedPresetId = presetId,
            customImageUri = customUri,
            blurRadiusDp = blur,
            brightness = brightness,
            opacity = opacity,
            overlayMode = overlayMode,
            overlayAlpha = overlayAlpha,
            position = pos,
            scope = scope,
            useDynamicColors = dynamicColors,
            themeMode = themeMode
        )
    }

    fun updateWallpaperSettings(settings: WallpaperSettings) {
        prefs.edit().apply {
            putString(KEY_TYPE, settings.type.name)
            putString(KEY_PRESET_ID, settings.selectedPresetId)
            putString(KEY_CUSTOM_URI, settings.customImageUri)
            putFloat(KEY_BLUR, settings.blurRadiusDp)
            putFloat(KEY_BRIGHTNESS, settings.brightness)
            putFloat(KEY_OPACITY, settings.opacity)
            putString(KEY_OVERLAY_MODE, settings.overlayMode.name)
            putFloat(KEY_OVERLAY_ALPHA, settings.overlayAlpha)
            putString(KEY_POSITION, settings.position.name)
            putString(KEY_SCOPE, settings.scope.name)
            putBoolean(KEY_DYNAMIC_COLORS, settings.useDynamicColors)
            putString(KEY_THEME_MODE, settings.themeMode.name)
            apply()
        }
        _wallpaperSettings.value = settings
    }

    fun setHasSeenOnboarding(seen: Boolean) {
        prefs.edit().putBoolean(KEY_HAS_SEEN_ONBOARDING, seen).apply()
        _hasSeenOnboarding.value = seen
    }

    private val _wifiOnlyDownloads = MutableStateFlow(prefs.getBoolean(KEY_WIFI_ONLY_DOWNLOADS, true))
    val wifiOnlyDownloads: StateFlow<Boolean> = _wifiOnlyDownloads.asStateFlow()

    private val _autoDownloadFavorites = MutableStateFlow(prefs.getBoolean(KEY_AUTO_DOWNLOAD_FAVORITES, false))
    val autoDownloadFavorites: StateFlow<Boolean> = _autoDownloadFavorites.asStateFlow()

    private val _streamingQuality = MutableStateFlow(prefs.getString(KEY_STREAMING_QUALITY, "320kbps High Quality") ?: "320kbps High Quality")
    val streamingQuality: StateFlow<String> = _streamingQuality.asStateFlow()

    private val _downloadQuality = MutableStateFlow(prefs.getString(KEY_DOWNLOAD_QUALITY, "320kbps MP3") ?: "320kbps MP3")
    val downloadQuality: StateFlow<String> = _downloadQuality.asStateFlow()

    private val _recentSearches = MutableStateFlow(loadRecentSearches())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private fun loadRecentSearches(): List<String> {
        val raw = prefs.getString(KEY_RECENT_SEARCHES, "") ?: ""
        return if (raw.isEmpty()) listOf("Midnight Horizon", "Lo-Fi Study", "Starlight Ensemble", "Synthwave")
        else raw.split("||").filter { it.isNotBlank() }
    }

    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        val current = _recentSearches.value.toMutableList()
        current.remove(trimmed)
        current.add(0, trimmed)
        val trimmedList = current.take(10)
        prefs.edit().putString(KEY_RECENT_SEARCHES, trimmedList.joinToString("||")).apply()
        _recentSearches.value = trimmedList
    }

    fun removeRecentSearch(query: String) {
        val current = _recentSearches.value.toMutableList()
        current.remove(query)
        prefs.edit().putString(KEY_RECENT_SEARCHES, current.joinToString("||")).apply()
        _recentSearches.value = current
    }

    fun clearRecentSearches() {
        prefs.edit().remove(KEY_RECENT_SEARCHES).apply()
        _recentSearches.value = emptyList()
    }

    fun setWifiOnlyDownloads(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WIFI_ONLY_DOWNLOADS, enabled).apply()
        _wifiOnlyDownloads.value = enabled
    }

    fun setAutoDownloadFavorites(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_DOWNLOAD_FAVORITES, enabled).apply()
        _autoDownloadFavorites.value = enabled
    }

    fun setStreamingQuality(quality: String) {
        prefs.edit().putString(KEY_STREAMING_QUALITY, quality).apply()
        _streamingQuality.value = quality
    }

    fun setDownloadQuality(quality: String) {
        prefs.edit().putString(KEY_DOWNLOAD_QUALITY, quality).apply()
        _downloadQuality.value = quality
    }

    fun setSleepTimer(minutes: Int?) {
        _sleepTimerMinutes.value = minutes
    }

    fun exportBackupJson(playlistsJson: String, historyCount: Int): String {
        val s = _wallpaperSettings.value
        return """
        {
          "version": 1,
          "appName": "Novatune",
          "exportedAt": ${System.currentTimeMillis()},
          "wallpaperSettings": {
            "type": "${s.type.name}",
            "presetId": "${s.selectedPresetId}",
            "customUri": "${s.customImageUri ?: ""}",
            "blur": ${s.blurRadiusDp},
            "brightness": ${s.brightness},
            "opacity": ${s.opacity},
            "overlayMode": "${s.overlayMode.name}",
            "overlayAlpha": ${s.overlayAlpha},
            "dynamicColors": ${s.useDynamicColors},
            "themeMode": "${s.themeMode.name}"
          },
          "playlists": $playlistsJson,
          "historyCount": $historyCount,
          "downloadSettings": {
            "wifiOnly": ${_wifiOnlyDownloads.value},
            "streamingQuality": "${_streamingQuality.value}",
            "downloadQuality": "${_downloadQuality.value}"
          }
        }
        """.trimIndent()
    }

    companion object {
        private const val KEY_HAS_SEEN_ONBOARDING = "has_seen_onboarding"
        private const val KEY_TYPE = "wp_type"
        private const val KEY_PRESET_ID = "wp_preset_id"
        private const val KEY_CUSTOM_URI = "wp_custom_uri"
        private const val KEY_BLUR = "wp_blur"
        private const val KEY_BRIGHTNESS = "wp_brightness"
        private const val KEY_OPACITY = "wp_opacity"
        private const val KEY_OVERLAY_MODE = "wp_overlay_mode"
        private const val KEY_OVERLAY_ALPHA = "wp_overlay_alpha"
        private const val KEY_POSITION = "wp_position"
        private const val KEY_SCOPE = "wp_scope"
        private const val KEY_DYNAMIC_COLORS = "wp_dynamic_colors"
        private const val KEY_THEME_MODE = "wp_theme_mode"
        private const val KEY_WIFI_ONLY_DOWNLOADS = "wifi_only_downloads"
        private const val KEY_AUTO_DOWNLOAD_FAVORITES = "auto_download_favorites"
        private const val KEY_STREAMING_QUALITY = "streaming_quality"
        private const val KEY_DOWNLOAD_QUALITY = "download_quality"
        private const val KEY_RECENT_SEARCHES = "recent_searches"
    }
}
