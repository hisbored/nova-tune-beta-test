package com.example.data.model

data class OnlineTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val coverUrl: String,
    val streamUrl: String,
    val downloadUrl: String? = null,
    val isDownloadPermitted: Boolean = true,
    val licenseType: String = "Creative Commons BY-NC 4.0",
    val licenseUrl: String = "https://creativecommons.org/licenses/by-nc/4.0/",
    val audioQualityTag: String = "320kbps MP3",
    val expectedSizeBytes: Long = 8_400_000L,
    val genre: String = "Synthwave",
    val releaseYear: Int = 2024,
    val isStreamOnly: Boolean = false,
    val playCount: Int = 14200,
    val lyrics: String? = null
)

data class OnlineArtist(
    val id: String,
    val name: String,
    val genre: String,
    val isVerified: Boolean = true,
    val avatarUrl: String,
    val monthlyListeners: String,
    val bio: String
)

data class OnlineAlbum(
    val id: String,
    val title: String,
    val artist: String,
    val releaseYear: Int,
    val coverUrl: String,
    val trackCount: Int,
    val genre: String
)

data class OnlinePlaylist(
    val id: String,
    val title: String,
    val description: String,
    val coverGradientStart: Long,
    val coverGradientEnd: Long,
    val trackCount: Int,
    val curator: String,
    val tracks: List<OnlineTrack> = emptyList()
)

enum class SearchFilterCategory(val label: String) {
    ALL("All"),
    SONGS("Songs"),
    ARTISTS("Artists"),
    ALBUMS("Albums"),
    GENRES("Genres"),
    PLAYLISTS("Playlists")
}

data class OnlineSearchResult(
    val query: String,
    val songs: List<OnlineTrack> = emptyList(),
    val artists: List<OnlineArtist> = emptyList(),
    val albums: List<OnlineAlbum> = emptyList(),
    val playlists: List<OnlinePlaylist> = emptyList(),
    val genres: List<String> = emptyList(),
    val totalCount: Int = 0
)

enum class DownloadState {
    PENDING,
    DOWNLOADING,
    PAUSED,
    FAILED,
    COMPLETED
}

data class DownloadProgressItem(
    val downloadId: String,
    val trackId: String,
    val title: String,
    val artist: String,
    val album: String,
    val coverUrl: String,
    val status: DownloadState,
    val progressPercent: Int,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val audioQuality: String,
    val localFilePath: String,
    val errorMessage: String? = null
)

data class StorageInfo(
    val totalSpaceBytes: Long,
    val freeSpaceBytes: Long,
    val novatuneDownloadsBytes: Long,
    val isLowStorage: Boolean
)
