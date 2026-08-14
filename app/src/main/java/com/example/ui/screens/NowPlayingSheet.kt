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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.audio.RepeatMode
import com.example.data.model.SongEntity
import com.example.ui.theme.AccentVioletPrimary

enum class NowPlayingSubView {
    NONE,
    LYRICS,
    QUEUE
}

@Composable
fun NowPlayingSheet(
    playbackState: PlaybackState,
    onCollapse: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleFavorite: (SongEntity) -> Unit,
    onAddToPlaylist: (SongEntity) -> Unit,
    onDownloadSong: (SongEntity) -> Unit,
    onShowSongDetails: (SongEntity) -> Unit,
    onOpenSleepTimer: () -> Unit,
    onSelectQueueSong: (SongEntity) -> Unit,
    onRetryPlayback: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val song = playbackState.currentSong ?: return
    var subView by remember { mutableStateOf(NowPlayingSubView.NONE) }
    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragSliderValue by remember { mutableFloatStateOf(0f) }

    val currentPosition = if (isDraggingSlider) {
        (dragSliderValue * song.durationMs).toLong()
    } else {
        playbackState.currentPositionMs
    }

    val elapsedText = remember(currentPosition) {
        val totalSec = currentPosition / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        String.format("%02d:%02d", min, sec)
    }

    val remainingText = remember(currentPosition, song.durationMs) {
        val remMs = (song.durationMs - currentPosition).coerceAtLeast(0L)
        val totalSec = remMs / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        String.format("-%02d:%02d", min, sec)
    }

    val sliderFraction = if (song.durationMs > 0) {
        (currentPosition.toFloat() / song.durationMs).coerceIn(0f, 1f)
    } else 0f

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = Color(0xFF0E0E0E).copy(alpha = 0.96f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Collapse Icon, Title & Source Badge, Queue Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onCollapse,
                    modifier = Modifier.testTag("now_playing_collapse_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse Now Playing",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (playbackState.isOnlineStream) Color(0xFF1565C0).copy(alpha = 0.25f) else Color(0xFF2E7D32).copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = if (playbackState.isOnlineStream) "ONLINE STREAM" else "OFFLINE VAULT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (playbackState.isOnlineStream) Color(0xFF90CAF9) else Color(0xFF81C784),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = song.album,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = {
                        subView = if (subView == NowPlayingSubView.QUEUE) NowPlayingSubView.NONE else NowPlayingSubView.QUEUE
                    },
                    modifier = Modifier.testTag("now_playing_queue_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Toggle Queue",
                        tint = if (subView == NowPlayingSubView.QUEUE) AccentVioletPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Playback Error Banner if any
            if (playbackState.playbackError != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    color = Color(0xFFFF5252).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = playbackState.playbackError,
                            color = Color(0xFFFF8A80),
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onRetryPlayback) {
                            Text("Retry", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Middle Content: Main Artwork or Lyrics or Queue
            when (subView) {
                NowPlayingSubView.LYRICS -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Lyrics",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentVioletPrimary
                                )
                                IconButton(onClick = { subView = NowPlayingSubView.NONE }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close Lyrics")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    Text(
                                        text = song.lyrics ?: "Lyrics are unavailable for this track.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        lineHeight = 32.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                NowPlayingSubView.QUEUE -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Up Next (${playbackState.queue.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentVioletPrimary
                                )
                                IconButton(onClick = { subView = NowPlayingSubView.NONE }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close Queue")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(playbackState.queue) { qSong ->
                                    val isCurrent = qSong.id == song.id
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isCurrent) AccentVioletPrimary.copy(alpha = 0.15f) else Color.Transparent
                                            )
                                            .clickable { onSelectQueueSong(qSong) }
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isCurrent) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                                            contentDescription = null,
                                            tint = if (isCurrent) AccentVioletPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = qSong.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isCurrent) AccentVioletPrimary else MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = qSong.artist,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                NowPlayingSubView.NONE -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Large Artwork Box with Vinyl Gradient & Glow
                        Box(
                            modifier = Modifier
                                .size(280.dp)
                                .shadow(24.dp, RoundedCornerShape(24.dp), ambientColor = AccentVioletPrimary, spotColor = AccentVioletPrimary)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF4A2B5E),
                                            Color(0xFF1A3C5E),
                                            Color(0xFF0E0E0E)
                                        )
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(24.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = song.title.take(1).uppercase(),
                                    fontSize = 72.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Black.copy(alpha = 0.4f)
                                ) {
                                    Text(
                                        text = song.qualityTag,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AccentVioletPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // Buffering Spinner Overlay
                            if (playbackState.isBuffering) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.4f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = AccentVioletPrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Audio Wave Visualizer Bars
                        Row(
                            modifier = Modifier
                                .height(28.dp)
                                .padding(horizontal = 32.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            playbackState.visualizerWave.forEach { amplitude ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(if (playbackState.isPlaying) amplitude.coerceIn(0.15f, 1f) else 0.15f)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    AccentVioletPrimary,
                                                    Color(0xFF8B5CF6)
                                                )
                                            )
                                        )
                                    )
                            }
                        }
                    }
                }
            }

            // Track Title & Favorite Action
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { onToggleFavorite(song) },
                        modifier = Modifier.testTag("now_playing_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (song.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (song.isFavorite) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Scrub Slider
                Slider(
                    value = if (isDraggingSlider) dragSliderValue else sliderFraction,
                    onValueChange = {
                        isDraggingSlider = true
                        dragSliderValue = it
                    },
                    onValueChangeFinished = {
                        onSeekTo((dragSliderValue * song.durationMs).toLong())
                        isDraggingSlider = false
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = AccentVioletPrimary,
                        activeTrackColor = AccentVioletPrimary,
                        inactiveTrackColor = Color.White.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("now_playing_scrub_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = elapsedText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = remainingText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Primary Playback Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle Button
                IconButton(
                    onClick = onToggleShuffle,
                    modifier = Modifier.testTag("now_playing_shuffle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playbackState.isShuffle) AccentVioletPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Previous Button
                IconButton(
                    onClick = onSkipPrevious,
                    modifier = Modifier.testTag("now_playing_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Play / Pause FAB
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(AccentVioletPrimary)
                        .clickable { onTogglePlayPause() }
                        .testTag("now_playing_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (playbackState.isBuffering) {
                        CircularProgressIndicator(
                            color = Color(0xFF381E72),
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                            tint = Color(0xFF381E72),
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                // Next Button
                IconButton(
                    onClick = onSkipNext,
                    modifier = Modifier.testTag("now_playing_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Repeat Button
                IconButton(
                    onClick = onCycleRepeat,
                    modifier = Modifier.testTag("now_playing_repeat_button")
                ) {
                    val icon = when (playbackState.repeatMode) {
                        RepeatMode.OFF -> Icons.Default.Repeat
                        RepeatMode.REPEAT_ALL -> Icons.Default.Repeat
                        RepeatMode.REPEAT_ONE -> Icons.Default.RepeatOne
                    }
                    val tint = if (playbackState.repeatMode != RepeatMode.OFF) AccentVioletPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    Icon(imageVector = icon, contentDescription = "Repeat Mode", tint = tint)
                }
            }

            // Bottom Actions Bar (Details, Download, Sleep timer, Add to playlist, Lyrics)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Song Details Info
                IconButton(
                    onClick = { onShowSongDetails(song) },
                    modifier = Modifier.testTag("now_playing_details_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Song Details",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Download Button (if online track and not yet downloaded)
                IconButton(
                    onClick = { onDownloadSong(song) },
                    modifier = Modifier.testTag("now_playing_download_button")
                ) {
                    Icon(
                        imageVector = if (song.isDownloaded) Icons.Filled.DownloadDone else Icons.Outlined.Download,
                        contentDescription = "Download Song",
                        tint = if (song.isDownloaded) Color(0xFF81C784) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Sleep Timer
                IconButton(
                    onClick = onOpenSleepTimer,
                    modifier = Modifier.testTag("now_playing_sleep_timer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bedtime,
                        contentDescription = "Sleep Timer",
                        tint = if (playbackState.sleepTimerSecondsRemaining != null) AccentVioletPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Add to Playlist
                IconButton(
                    onClick = { onAddToPlaylist(song) },
                    modifier = Modifier.testTag("now_playing_add_playlist_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlaylistAdd,
                        contentDescription = "Add to playlist",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Lyrics Toggle
                IconButton(
                    onClick = {
                        subView = if (subView == NowPlayingSubView.LYRICS) NowPlayingSubView.NONE else NowPlayingSubView.LYRICS
                    },
                    modifier = Modifier.testTag("now_playing_lyrics_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Article,
                        contentDescription = "View lyrics",
                        tint = if (subView == NowPlayingSubView.LYRICS) AccentVioletPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
