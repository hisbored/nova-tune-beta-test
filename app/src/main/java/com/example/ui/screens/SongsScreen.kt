package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.data.model.SongEntity
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.AccentVioletPrimary

enum class SongSortOption(val label: String) {
    TITLE("Title (A-Z)"),
    ARTIST("Artist"),
    ALBUM("Album"),
    DURATION("Duration"),
    DATE_ADDED("Recently Added")
}

enum class SongFilterOption(val label: String) {
    ALL("All Tracks"),
    FAVORITES("Favorites"),
    DOWNLOADED("Offline"),
    HIGH_RES("High Quality")
}

@Composable
fun SongsScreen(
    songs: List<SongEntity>,
    playbackState: PlaybackState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSongClick: (SongEntity, List<SongEntity>) -> Unit,
    onToggleFavorite: (SongEntity) -> Unit,
    onPlayNext: (SongEntity) -> Unit,
    onAddToQueue: (SongEntity) -> Unit,
    onAddToPlaylist: (SongEntity) -> Unit,
    onDownload: (SongEntity) -> Unit,
    onDeleteSong: (SongEntity) -> Unit,
    onRescanLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sortOption by remember { mutableStateOf(SongSortOption.TITLE) }
    var filterOption by remember { mutableStateOf(SongFilterOption.ALL) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Filter & Sort Logic
    val filteredSongs = remember(songs, searchQuery, sortOption, filterOption) {
        var list = songs

        // Apply Search
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.artist.lowercase().contains(q) ||
                it.album.lowercase().contains(q)
            }
        }

        // Apply Filter
        list = when (filterOption) {
            SongFilterOption.ALL -> list
            SongFilterOption.FAVORITES -> list.filter { it.isFavorite }
            SongFilterOption.DOWNLOADED -> list.filter { it.isDownloaded }
            SongFilterOption.HIGH_RES -> list.filter { it.qualityTag.contains("FLAC") || it.qualityTag.contains("320") }
        }

        // Apply Sort
        when (sortOption) {
            SongSortOption.TITLE -> list.sortedBy { it.title.lowercase() }
            SongSortOption.ARTIST -> list.sortedBy { it.artist.lowercase() }
            SongSortOption.ALBUM -> list.sortedBy { it.album.lowercase() }
            SongSortOption.DURATION -> list.sortedByDescending { it.durationMs }
            SongSortOption.DATE_ADDED -> list.sortedByDescending { it.dateAdded }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search songs, artists, albums...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedBorderColor = AccentVioletPrimary,
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("songs_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips Row & Sort Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SongFilterOption.entries.forEach { opt ->
                    val isSelected = filterOption == opt
                    FilterChip(
                        selected = isSelected,
                        onClick = { filterOption = opt },
                        label = { Text(opt.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentVioletPrimary,
                            selectedLabelColor = Color(0xFF381E72)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Sort Dropdown Menu
            Box {
                IconButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier.testTag("sort_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort tracks",
                        tint = AccentVioletPrimary
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    SongSortOption.entries.forEach { opt ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = opt.label,
                                    color = if (sortOption == opt) AccentVioletPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (sortOption == opt) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            trailingIcon = {
                                if (sortOption == opt) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = AccentVioletPrimary)
                                }
                            },
                            onClick = {
                                sortOption = opt
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tracks Counter & Rescan Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredSongs.size} Tracks",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextButton(
                onClick = onRescanLibrary,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = AccentVioletPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Rescan Device", color = AccentVioletPrimary, fontSize = 12.sp)
            }
        }

        // Song List
        if (filteredSongs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MusicOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No tracks match \"$searchQuery\"" else "No songs found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(filteredSongs, key = { it.id }) { song ->
                    val isCurrent = playbackState.currentSong?.id == song.id
                    TrackRowItem(
                        song = song,
                        isPlaying = playbackState.isPlaying,
                        isCurrentTrack = isCurrent,
                        onTrackClick = { onSongClick(song, filteredSongs) },
                        onToggleFavorite = { onToggleFavorite(song) },
                        onPlayNext = { onPlayNext(song) },
                        onAddToQueue = { onAddToQueue(song) },
                        onAddToPlaylist = { onAddToPlaylist(song) },
                        onDownload = { onDownload(song) },
                        onDelete = { onDeleteSong(song) }
                    )
                }
            }
        }
    }
}
