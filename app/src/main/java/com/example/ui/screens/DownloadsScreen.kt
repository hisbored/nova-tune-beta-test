package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.data.model.*
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.AccentVioletActivePill
import com.example.ui.theme.AccentVioletPrimary

@Composable
fun DownloadsScreen(
    downloads: List<DownloadEntity>,
    activeDownloads: Map<String, DownloadProgressItem>,
    downloadedSongs: List<SongEntity>,
    storageInfo: StorageInfo,
    playbackState: PlaybackState,
    onSongClick: (SongEntity, List<SongEntity>) -> Unit,
    onToggleFavorite: (SongEntity) -> Unit,
    onPlayNext: (SongEntity) -> Unit,
    onAddToQueue: (SongEntity) -> Unit,
    onAddToPlaylist: (SongEntity) -> Unit,
    onPauseDownload: (String) -> Unit,
    onResumeDownload: (DownloadProgressItem) -> Unit,
    onCancelDownload: (String) -> Unit,
    onDeleteDownload: (DownloadEntity) -> Unit,
    onRescanDownloads: () -> Unit,
    modifier: Modifier = Modifier
) {
    var downloadToDelete by remember { mutableStateOf<DownloadEntity?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = All/Completed, 1 = Active Queue, 2 = Offline Songs
    var sortBy by remember { mutableStateOf("Date") } // Date, Title, Artist, Size

    val sortedDownloads = remember(downloads, sortBy) {
        when (sortBy) {
            "Title" -> downloads.sortedBy { it.title.lowercase() }
            "Artist" -> downloads.sortedBy { it.artist.lowercase() }
            "Size" -> downloads.sortedByDescending { it.sizeBytes }
            else -> downloads.sortedByDescending { it.downloadedAt }
        }
    }

    val totalNovatuneMb = (storageInfo.novatuneDownloadsBytes / (1024 * 1024)).coerceAtLeast(downloads.sumOf { it.sizeBytes } / (1024 * 1024))
    val freeGb = storageInfo.freeSpaceBytes / (1024 * 1024 * 1024.0)
    val totalGb = (storageInfo.totalSpaceBytes / (1024 * 1024 * 1024.0)).coerceAtLeast(32.0)
    val usedRatio = ((storageInfo.totalSpaceBytes - storageInfo.freeSpaceBytes).toFloat() / storageInfo.totalSpaceBytes.toFloat()).coerceIn(0.1f, 0.98f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Downloads & Offline",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(
                onClick = onRescanDownloads,
                modifier = Modifier.testTag("rescan_downloads_button")
            ) {
                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentVioletPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sync Storage", color = AccentVioletPrimary, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Storage Usage Card with Low Storage Warning
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp)),
            color = Color(0xFF1C1B1F),
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Novatune Offline Vault",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Path: Music/Novatune/",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AccentVioletPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "$totalNovatuneMb MB used",
                            color = AccentVioletPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar for Device Storage
                LinearProgressIndicator(
                    progress = { usedRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (storageInfo.isLowStorage) Color(0xFFFF5252) else AccentVioletPrimary,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${String.format("%.1f", freeGb)} GB Free of ${String.format("%.1f", totalGb)} GB",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${downloads.size} Offline Tracks",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Low storage warning
                if (storageInfo.isLowStorage) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFF5252).copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Device storage is nearly full (< 500 MB remaining)",
                                color = Color(0xFFFF8A80),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector (All Downloads / Active Queue / Local Offline)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf(
                "Completed (${downloads.size})" to 0,
                "Active Queue (${activeDownloads.size})" to 1,
                "Device Audio (${downloadedSongs.size})" to 2
            )
            tabs.forEach { (title, index) ->
                val isSelected = selectedTab == index
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) AccentVioletActivePill else Color(0xFF222026),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) AccentVioletPrimary else Color.White.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = index }
                        .testTag("downloads_tab_$index")
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) AccentVioletPrimary else Color.White.copy(alpha = 0.75f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content Area
        when (selectedTab) {
            1 -> {
                // Active Queue Tab
                if (activeDownloads.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No active downloads in progress",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(activeDownloads.values.toList()) { item ->
                            ActiveDownloadCard(
                                item = item,
                                onPause = { onPauseDownload(item.downloadId) },
                                onResume = { onResumeDownload(item) },
                                onCancel = { onCancelDownload(item.downloadId) }
                            )
                        }
                    }
                }
            }
            2 -> {
                // Device Audio Songs
                if (downloadedSongs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No offline songs stored yet",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        items(downloadedSongs) { song ->
                            val isPlaying = playbackState.currentSong?.id == song.id && playbackState.isPlaying
                            TrackRowItem(
                                song = song,
                                isPlaying = isPlaying,
                                onClick = { onSongClick(song, downloadedSongs) },
                                onToggleFavorite = { onToggleFavorite(song) },
                                onPlayNext = { onPlayNext(song) },
                                onAddToQueue = { onAddToQueue(song) },
                                onAddToPlaylist = { onAddToPlaylist(song) },
                                onDownload = {}
                            )
                        }
                    }
                }
            }
            else -> {
                // Completed Downloads List
                if (sortedDownloads.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DownloadForOffline,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No downloaded tracks yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Search authorized online music or browse recommendations to download tracks for offline listening.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.6f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Sort Controls Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${sortedDownloads.size} Downloaded Songs",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentVioletPrimary
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Sort: ", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                            listOf("Date", "Title", "Artist", "Size").forEach { s ->
                                Text(
                                    text = s,
                                    fontSize = 11.sp,
                                    fontWeight = if (sortBy == s) FontWeight.Bold else FontWeight.Normal,
                                    color = if (sortBy == s) AccentVioletPrimary else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .clickable { sortBy = s }
                                        .padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(sortedDownloads) { download ->
                            DownloadedItemRow(
                                download = download,
                                onPlay = {
                                    // Match or convert to SongEntity and play
                                    val matched = downloadedSongs.find { it.title.equals(download.title, ignoreCase = true) }
                                    val songToPlay = matched ?: SongEntity(
                                        id = (download.downloadId.hashCode().toLong() and 0x7FFFFFFF),
                                        title = download.title,
                                        artist = download.artist,
                                        album = download.album,
                                        durationMs = download.durationMs,
                                        uriString = if (download.localFilePath.startsWith("file://")) download.localFilePath else "file://${download.localFilePath}",
                                        isDownloaded = true,
                                        qualityTag = download.audioQuality
                                    )
                                    onSongClick(songToPlay, downloadedSongs.ifEmpty { listOf(songToPlay) })
                                },
                                onDelete = {
                                    downloadToDelete = download
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    downloadToDelete?.let { download ->
        AlertDialog(
            onDismissRequest = { downloadToDelete = null },
            icon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFFF5252)) },
            title = { Text("Delete Downloaded Song?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${download.title}\" by ${download.artist}? The audio file will be permanently removed from your device storage.",
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteDownload(download)
                        downloadToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                    modifier = Modifier.testTag("confirm_delete_download_button")
                ) {
                    Text("Delete File", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { downloadToDelete = null }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF222026),
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun ActiveDownloadCard(
    item: DownloadProgressItem,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        color = Color(0xFF1E1C22),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${item.artist} • ${item.audioQuality}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (item.status == DownloadState.DOWNLOADING) {
                        IconButton(onClick = onPause, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Pause, contentDescription = "Pause", tint = AccentVioletPrimary, modifier = Modifier.size(18.dp))
                        }
                    } else if (item.status == DownloadState.PAUSED || item.status == DownloadState.FAILED) {
                        IconButton(onClick = onResume, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Resume", tint = AccentVioletPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                    IconButton(onClick = onCancel, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (item.progressPercent / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = when (item.status) {
                    DownloadState.FAILED -> Color(0xFFFF5252)
                    DownloadState.PAUSED -> Color(0xFFFFB74D)
                    else -> AccentVioletPrimary
                },
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${item.status.name} • ${item.progressPercent}%",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                val dlMb = item.downloadedBytes / (1024 * 1024.0)
                val totMb = item.totalBytes / (1024 * 1024.0)
                Text(
                    text = "${String.format("%.1f", dlMb)} / ${String.format("%.1f", totMb)} MB",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun DownloadedItemRow(
    download: DownloadEntity,
    onPlay: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sizeMb = download.sizeBytes / (1024 * 1024.0)
    val minutes = download.durationMs / 1000 / 60
    val seconds = (download.durationMs / 1000) % 60
    val durationStr = String.format("%d:%02d", minutes, seconds)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onPlay() }
            .testTag("download_row_${download.downloadId}"),
        color = Color(0xFF1E1C22),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2C253B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = AccentVioletPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = download.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${download.artist} • ${download.album}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$durationStr • ${String.format("%.1f", sizeMb)} MB • ${download.audioQuality}",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.45f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF2E7D32).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "OFFLINE",
                            color = Color(0xFF81C784),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = onPlay,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = AccentVioletPrimary)
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.5f))
            }
        }
    }
}
