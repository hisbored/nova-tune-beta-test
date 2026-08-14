package com.example.data.model

enum class WallpaperType {
    PRESET,
    GALLERY_URI,
    DEFAULT_AMBIENT
}

enum class OverlayMode {
    AUTO,
    DARK,
    LIGHT
}

enum class WallpaperPosition {
    CENTER,
    TOP,
    BOTTOM
}

enum class WallpaperScope {
    APP_WIDE,
    NOW_PLAYING_ONLY
}

enum class AppThemeMode {
    SYSTEM,
    DARK,
    LIGHT,
    AMOLED
}

data class WallpaperPreset(
    val id: String,
    val name: String,
    val description: String,
    val gradientStartColor: Long,
    val gradientEndColor: Long,
    val accentColor: Long,
    val previewGradient: List<Long>
)

object WallpaperPresets {
    val presets = listOf(
        WallpaperPreset(
            id = "preset_nebula",
            name = "Deep Nebula",
            description = "Ethereal cosmic indigo and luminous violet",
            gradientStartColor = 0xFF4A2B5E,
            gradientEndColor = 0xFF1A3C5E,
            accentColor = 0xFFD0BCFF,
            previewGradient = listOf(0xFF4A2B5E, 0xFF2D1B4E, 0xFF1A3C5E)
        ),
        WallpaperPreset(
            id = "preset_obsidian",
            name = "Obsidian Velvet",
            description = "Deep dark luxury amethyst glow",
            gradientStartColor = 0xFF240B36,
            gradientEndColor = 0xFF0D1117,
            accentColor = 0xFFC084FC,
            previewGradient = listOf(0xFF240B36, 0xFF170928, 0xFF0D1117)
        ),
        WallpaperPreset(
            id = "preset_aurora",
            name = "Cyber Aurora",
            description = "Electric emerald & deep oceanic teal",
            gradientStartColor = 0xFF064E3B,
            gradientEndColor = 0xFF0F172A,
            accentColor = 0xFF34D399,
            previewGradient = listOf(0xFF064E3B, 0xFF042F2E, 0xFF0F172A)
        ),
        WallpaperPreset(
            id = "preset_solar",
            name = "Solar Twilight",
            description = "Warm burnt amber and crimson dusk",
            gradientStartColor = 0xFF581C87,
            gradientEndColor = 0xFF7C2D12,
            accentColor = 0xFFF97316,
            previewGradient = listOf(0xFF581C87, 0xFF701A75, 0xFF7C2D12)
        ),
        WallpaperPreset(
            id = "preset_amoled",
            name = "Pure AMOLED",
            description = "Zero-emissive deep obsidian black",
            gradientStartColor = 0xFF050505,
            gradientEndColor = 0xFF0E0E0E,
            accentColor = 0xFFE2E8F0,
            previewGradient = listOf(0xFF050505, 0xFF0A0A0A, 0xFF0E0E0E)
        )
    )

    fun getById(id: String): WallpaperPreset {
        return presets.find { it.id == id } ?: presets.first()
    }
}

data class WallpaperSettings(
    val type: WallpaperType = WallpaperType.PRESET,
    val selectedPresetId: String = "preset_nebula",
    val customImageUri: String? = null,
    val blurRadiusDp: Float = 20f,
    val brightness: Float = 0.85f,
    val opacity: Float = 0.50f,
    val overlayMode: OverlayMode = OverlayMode.AUTO,
    val overlayAlpha: Float = 0.35f,
    val position: WallpaperPosition = WallpaperPosition.CENTER,
    val scope: WallpaperScope = WallpaperScope.APP_WIDE,
    val useDynamicColors: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.DARK
)
