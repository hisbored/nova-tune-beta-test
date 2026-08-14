package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.audio.PlaybackState
import com.example.data.model.OnlinePlaylist
import com.example.data.model.OnlineTrack
import com.example.data.model.PlaylistEntity
import com.example.data.model.SongEntity
import com.example.ui.components.NovatuneTab
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.AccentVioletPrimary

@Composable
fun HomeScreen(
    recentlyPlayed: List<SongEntity>,
    recentlyAdded: List<SongEntity>,
    favoriteSongs: List<SongEntity>,
    downloadedSongs: List<SongEntity>,
    playlists: List<PlaylistEntity>,
    featuredOnlineTracks: List<OnlineTrack>,
    curatedOnlinePlaylists: List<OnlinePlaylist>,
    playbackState: PlaybackState,
    onNavigateTab: (NovatuneTab) -> Unit,
    onSongClick: (SongEntity, List<SongEntity>) -> Unit,
    onPlayOnlineTrack: (OnlineTrack) -> Unit,
    onDownloadOnlineTrack: (OnlineTrack) -> Unit,
    onShowTrackDetails: (OnlineTrack) -> Unit,
    onOpenOnlinePlaylist: (OnlinePlaylist) -> Unit,
    onToggleFavorite: (SongEntity) -> Unit,
    onPlayNext: (SongEntity) -> Unit,
    onAddToQueue: (SongEntity) -> Unit,
    onAddToPlaylist: (SongEntity) -> Unit,
    onDownload: (SongEntity) -> Unit,
    onOpenPlaylist: (PlaylistEntity) -> Unit,
    onCreatePlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
    ) {
        // Header Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NOW STREAMING",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentVioletPrimary,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Novatune",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Search Shortcut Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.06f))
                            .clickable { onNavigateTab(NovatuneTab.SEARCH) }
                            .testTag("home_search_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search music",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Settings Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.06f))
                            .clickable { onNavigateTab(NovatuneTab.SETTINGS) }
                            .testTag("home_settings_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Online Search Bar on Home Screen
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateTab(NovatuneTab.SEARCH) }
                    .testTag("home_search_bar_surface"),
                color = Color(0xFF1C1B1F),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AccentVioletPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Search authorized songs, artists, albums...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Recommended Authorized Releases Section (Online Catalog)
        if (featuredOnlineTracks.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "RECOMMENDED RELEASES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentVioletPrimary,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Authorized Online Music",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    TextButton(onClick = { onNavigateTab(NovatuneTab.SEARCH) }) {
                        Text("See All", color = AccentVioletPrimary, fontSize = 13.sp)
                    }
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(featuredOnlineTracks) { track ->
                        Surface(
                            modifier = Modifier
                                .width(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onPlayOnlineTrack(track) }
                                .testTag("home_featured_track_${track.id}"),
                            color = Color(0xFF1E1C22),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(140.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF2C253B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = track.coverUrl,
                                        contentDescription = track.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    // Play Button Overlay
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(6.dp)
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(AccentVioletPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Stream",
                                            tint = Color(0xFF1C1B1F),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = track.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.6f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = track.audioQualityTag,
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.4f)
                                    )
                                    if (track.isDownloadPermitted) {
                                        IconButton(
                                            onClick = { onDownloadOnlineTrack(track) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Download,
                                                contentDescription = "Download",
                                                tint = Color.White.copy(alpha = 0.7f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Stats / Mood Shortcuts
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickStatCard(
                    title = "Favorites",
                    count = "${favoriteSongs.size} tracks",
                    icon = Icons.Default.Favorite,
                    gradient = listOf(Color(0xFF833AB4), Color(0xFFFD1D1D)),
                    onClick = { onNavigateTab(NovatuneTab.SONGS) },
                    modifier = Modifier.weight(1f)
                )

                QuickStatCard(
                    title = "Downloaded",
                    count = "${downloadedSongs.size} offline",
                    icon = Icons.Default.DownloadDone,
                    gradient = listOf(Color(0xFF00B4DB), Color(0xFF0083B0)),
                    onClick = { onNavigateTab(NovatuneTab.DOWNLOADS) },
                    modifier = Modifier.weight(1f)
                )

                QuickStatCard(
                    title = "Playlists",
                    count = "${playlists.size} created",
                    icon = Icons.Default.QueueMusic,
                    gradient = listOf(Color(0xFF11998E), Color(0xFF38EF7D)),
                    onClick = { onNavigateTab(NovatuneTab.PLAYLISTS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Curated Mood & Genre Playlists
        if (curatedOnlinePlaylists.isNotEmpty()) {
            item {
                Text(
                    text = "Curated Genre Playlists",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(curatedOnlinePlaylists) { playlist ->
                        Surface(
                            modifier = Modifier
                                .width(200.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onOpenOnlinePlaylist(playlist) },
                            color = Color(0xFF1E1C22),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(playlist.coverGradientStart).copy(alpha = 0.7f),
                                                Color(playlist.coverGradientEnd).copy(alpha = 0.85f)
                                            )
                                        )
                                    )
                                    .padding(12.dp)
                            ) {
                                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                    Text(
                                        text = playlist.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${playlist.trackCount} authorized tracks",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recently Played Section
        if (recentlyPlayed.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recently Played",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "${recentlyPlayed.size} tracks",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(recentlyPlayed.take(5)) { song ->
                val isCurrentlyPlaying = playbackState.currentSong?.id == song.id && playbackState.isPlaying
                TrackRowItem(
                    song = song,
                    isPlaying = isCurrentlyPlaying,
                    onClick = { onSongClick(song, recentlyPlayed) },
                    onToggleFavorite = { onToggleFavorite(song) },
                    onPlayNext = { onPlayNext(song) },
                    onAddToQueue = { onAddToQueue(song) },
                    onAddToPlaylist = { onAddToPlaylist(song) },
                    onDownload = { onDownload(song) }
                )
            }
        }

        // Recently Added Local Tracks
        if (recentlyAdded.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Device & Downloaded Library",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    TextButton(onClick = { onNavigateTab(NovatuneTab.SONGS) }) {
                        Text("View All", color = AccentVioletPrimary, fontSize = 13.sp)
                    }
                }
            }

            items(recentlyAdded.take(5)) { song ->
                val isCurrentlyPlaying = playbackState.currentSong?.id == song.id && playbackState.isPlaying
                TrackRowItem(
                    song = song,
                    isPlaying = isCurrentlyPlaying,
                    onClick = { onSongClick(song, recentlyAdded) },
                    onToggleFavorite = { onToggleFavorite(song) },
                    onPlayNext = { onPlayNext(song) },
                    onAddToQueue = { onAddToQueue(song) },
                    onAddToPlaylist = { onAddToPlaylist(song) },
                    onDownload = { onDownload(song) }
                )
            }
        }
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(84.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp)),
        color = Color(0xFF1C1B1F),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = count,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}
