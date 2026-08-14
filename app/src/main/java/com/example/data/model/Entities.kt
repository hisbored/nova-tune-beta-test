package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long = 0L,
    val durationMs: Long,
    val uriString: String,
    val mimeType: String = "audio/mpeg",
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis(),
    val playCount: Int = 0,
    val lastPlayedTimestamp: Long = 0L,
    val qualityTag: String = "320kbps MP3",
    val lyrics: String? = null
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Long = 0L,
    val title: String,
    val description: String = "",
    val coverGradientStart: Long = 0xFF4A2B5EL,
    val coverGradientEnd: Long = 0xFF1A3C5EL,
    val coverIconName: String = "playlist",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlist_songs", primaryKeys = ["playlistId", "songId"])
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long,
    val sortOrder: Int = 0
)

@Entity(tableName = "playback_history")
data class PlaybackHistoryEntity(
    @PrimaryKey(autoGenerate = true) val historyId: Long = 0L,
    val songId: Long,
    val playedAt: Long = System.currentTimeMillis(),
    val positionMs: Long = 0L
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val downloadId: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val sourceUrl: String,
    val localFilePath: String,
    val status: String = "COMPLETED", // PENDING, DOWNLOADING, PAUSED, COMPLETED, FAILED
    val progressPercent: Int = 100,
    val sizeBytes: Long = 8_400_000L,
    val audioQuality: String = "320kbps MP3",
    val licenseTag: String = "CC BY-NC 4.0",
    val downloadedAt: Long = System.currentTimeMillis()
)

fun OnlineTrack.toSongEntity(isDownloaded: Boolean = false, localUri: String? = null): SongEntity {
    val numericId = (id.hashCode().toLong() and 0x7FFFFFFF) + 50000L
    return SongEntity(
        id = numericId,
        title = title,
        artist = artist,
        album = album,
        albumId = (album.hashCode().toLong() and 0x7FFFFFFF),
        durationMs = durationMs,
        uriString = localUri ?: streamUrl,
        mimeType = "audio/mpeg",
        isFavorite = false,
        isDownloaded = isDownloaded,
        dateAdded = System.currentTimeMillis(),
        qualityTag = audioQualityTag,
        lyrics = lyrics
    )
}

