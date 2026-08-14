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
import androidx.compose.runtime.*
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
import com.example.data.model.*
import com.example.ui.theme.AccentVioletActivePill
import com.example.ui.theme.AccentVioletPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: SearchFilterCategory,
    onCategoryChange: (SearchFilterCategory) -> Unit,
    searchResults: OnlineSearchResult,
    isLoading: Boolean,
    recentSearches: List<String>,
    onSelectRecentSearch: (String) -> Unit,
    onRemoveRecentSearch: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onPlayTrack: (OnlineTrack) -> Unit,
    onDownloadTrack: (OnlineTrack) -> Unit,
    onToggleFavorite: (OnlineTrack) -> Unit,
    onAddToPlaylist: (OnlineTrack) -> Unit,
    onShowTrackDetails: (OnlineTrack) -> Unit,
    onReportContent: (OnlineTrack) -> Unit,
    onPlayArtistTracks: (OnlineArtist) -> Unit,
    onPlayAlbumTracks: (OnlineAlbum) -> Unit,
    onOpenPlaylist: (OnlinePlaylist) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filterTabs = SearchFilterCategory.entries

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search Header Title
        Text(
            text = "Search Catalog",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_text_field"),
            placeholder = {
                Text(
                    text = "Search songs, artists, albums, genres...",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = AccentVioletPrimary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.testTag("search_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1C1B1F),
                unfocusedContainerColor = Color(0xFF1C1B1F),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = AccentVioletPrimary,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Category Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterTabs) { category ->
                val isSelected = category == selectedCategory
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) AccentVioletActivePill else Color(0xFF222026),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) AccentVioletPrimary else Color.White.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier
                        .clickable { onCategoryChange(category) }
                        .testTag("search_filter_${category.name.lowercase()}")
                ) {
                    Text(
                        text = category.label,
                        color = if (isSelected) AccentVioletPrimary else Color.White.copy(alpha = 0.8f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Content
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = AccentVioletPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Searching authorized catalog...",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else if (searchQuery.isEmpty()) {
            // Initial view: Recent Searches & Suggestions
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                if (recentSearches.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Searches",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(
                                onClick = onClearRecentSearches,
                                modifier = Modifier.testTag("clear_recent_searches_button")
                            ) {
                                Text("Clear All", color = AccentVioletPrimary, fontSize = 12.sp)
                            }
                        }
                    }

                    items(recentSearches) { query ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectRecentSearch(query) },
                            color = Color(0xFF1E1C22)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = query,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { onRemoveRecentSearch(query) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = Color.White.copy(alpha = 0.4f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Explore Genres & Moods",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    val sampleGenres = listOf(
                        "Synthwave", "Lo-Fi Beats", "Ambient Sleep", "Cyberpunk",
                        "Classical Chamber", "Electronic Odyssey", "Chillhop Study", "Space Drone"
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sampleGenres.forEach { genre ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF2B2833),
                                modifier = Modifier.clickable {
                                    onSearchQueryChange(genre)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = AccentVioletPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = genre,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else if (searchResults.totalCount == 0) {
            // Empty Results State
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
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No authorized results found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try searching for artists like Aether Wave, Starlight Ensemble, or genres like Synthwave and Lo-Fi.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentVioletPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset Search", color = Color(0xFF1C1B1F), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Display Results
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // Songs Section
                if (searchResults.songs.isNotEmpty()) {
                    item {
                        Text(
                            text = "Songs (${searchResults.songs.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentVioletPrimary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(searchResults.songs) { track ->
                        OnlineTrackResultCard(
                            track = track,
                            onPlay = { onPlayTrack(track) },
                            onDownload = { onDownloadTrack(track) },
                            onToggleFavorite = { onToggleFavorite(track) },
                            onAddToPlaylist = { onAddToPlaylist(track) },
                            onShowDetails = { onShowTrackDetails(track) },
                            onReport = { onReportContent(track) }
                        )
                    }
                }

                // Artists Section
                if (searchResults.artists.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Artists (${searchResults.artists.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentVioletPrimary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(searchResults.artists) { artist ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onPlayArtistTracks(artist) },
                            color = Color(0xFF1E1C22)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF3B2D54)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = artist.avatarUrl,
                                        contentDescription = artist.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = artist.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        if (artist.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Verified",
                                                tint = Color(0xFF81D4FA),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${artist.genre} • ${artist.monthlyListeners} listeners",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }

                                Button(
                                    onClick = { onPlayArtistTracks(artist) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentVioletPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF1C1B1F))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Play", fontSize = 12.sp, color = Color(0xFF1C1B1F), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Albums Section
                if (searchResults.albums.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Albums (${searchResults.albums.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentVioletPrimary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(searchResults.albums) { album ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onPlayAlbumTracks(album) },
                            color = Color(0xFF1E1C22)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF2A203B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = album.coverUrl,
                                        contentDescription = album.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = album.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${album.artist} • ${album.releaseYear} • ${album.trackCount} tracks",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }

                                IconButton(onClick = { onPlayAlbumTracks(album) }) {
                                    Icon(Icons.Default.PlayCircle, contentDescription = "Play Album", tint = AccentVioletPrimary)
                                }
                            }
                        }
                    }
                }

                // Playlists Section
                if (searchResults.playlists.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Curated Playlists (${searchResults.playlists.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentVioletPrimary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(searchResults.playlists) { playlist ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onOpenPlaylist(playlist) },
                            color = Color(0xFF1E1C22)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(playlist.coverGradientStart), Color(playlist.coverGradientEnd))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QueueMusic,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = playlist.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${playlist.curator} • ${playlist.trackCount} tracks",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }

                                IconButton(onClick = { onOpenPlaylist(playlist) }) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play Playlist", tint = AccentVioletPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnlineTrackResultCard(
    track: OnlineTrack,
    onPlay: () -> Unit,
    onDownload: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onShowDetails: () -> Unit,
    onReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    val minutes = track.durationMs / 1000 / 60
    val seconds = (track.durationMs / 1000) % 60
    val durationStr = String.format("%d:%02d", minutes, seconds)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onPlay() }
            .testTag("online_track_card_${track.id}"),
        color = Color(0xFF1C1B1F),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Artwork
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF2C253B)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = track.coverUrl,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Metadata Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${track.artist} • ${track.album}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$durationStr • ${track.audioQualityTag}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.45f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Availability Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (track.isDownloadPermitted) Color(0xFF2E7D32).copy(alpha = 0.25f) else Color(0xFFE65100).copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = if (track.isDownloadPermitted) "Stream & DL" else "Stream-Only",
                            color = if (track.isDownloadPermitted) Color(0xFF81C784) else Color(0xFFFFB74D),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Quick Stream Button
            IconButton(
                onClick = onPlay,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AccentVioletPrimary.copy(alpha = 0.15f))
                    .testTag("stream_track_button_${track.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Stream",
                    tint = AccentVioletPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Download button (when authorized)
            if (track.isDownloadPermitted) {
                IconButton(
                    onClick = onDownload,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("download_track_button_${track.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 3-dots More Options Menu
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color(0xFF25232A))
                ) {
                    DropdownMenuItem(
                        text = { Text("View Song Details", color = Color.White) },
                        leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null, tint = AccentVioletPrimary) },
                        onClick = {
                            showMenu = false
                            onShowDetails()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add to Playlist", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.PlaylistAdd, contentDescription = null, tint = Color.White) },
                        onClick = {
                            showMenu = false
                            onAddToPlaylist()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add to Favorites", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.White) },
                        onClick = {
                            showMenu = false
                            onToggleFavorite()
                        }
                    )
                    if (track.isDownloadPermitted) {
                        DropdownMenuItem(
                            text = { Text("Download Track", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Download, contentDescription = null, tint = Color.White) },
                            onClick = {
                                showMenu = false
                                onDownload()
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Report Content / DMCA", color = Color(0xFFFF8A80)) },
                        leadingIcon = { Icon(Icons.Outlined.ReportProblem, contentDescription = null, tint = Color(0xFFFF8A80)) },
                        onClick = {
                            showMenu = false
                            onReport()
                        }
                    )
                }
            }
        }
    }
}
