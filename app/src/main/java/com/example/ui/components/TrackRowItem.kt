package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.model.SongEntity
import com.example.ui.theme.AccentVioletPrimary

@Composable
fun TrackRowItem(
    song: SongEntity,
    isPlaying: Boolean,
    isCurrentTrack: Boolean,
    onTrackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onDownload: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    val formattedDuration = remember(song.durationMs) {
        val totalSec = song.durationMs / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        String.format("%02d:%02d", min, sec)
    }

    // Determine consistent gradient per track ID
    val gradientColors = remember(song.id) {
        when ((song.id % 4).toInt()) {
            0 -> listOf(Color(0xFF6366F1), Color(0xFF9333EA))
            1 -> listOf(Color(0xFF10B981), Color(0xFF0D9488))
            2 -> listOf(Color(0xFFF97316), Color(0xFFE11D48))
            else -> listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("track_row_${song.id}")
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isCurrentTrack) Color.White.copy(alpha = 0.08f) else Color.Transparent
            )
            .clickable { onTrackClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail Artwork
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            if (isCurrentTrack && isPlaying) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Playing Indicator",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = song.title.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Info Block
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isCurrentTrack) AccentVioletPrimary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isCurrentTrack) FontWeight.Bold else FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${song.artist} • ${song.album}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Quality & Duration Badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = formattedDuration,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }

        // Favorite Toggle Icon Button
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier
                .size(36.dp)
                .testTag("favorite_button_${song.id}")
        ) {
            Icon(
                imageVector = if (song.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (song.isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (song.isFavorite) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        // Context Overflow Menu Button
        Box {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("overflow_menu_button_${song.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Track options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                DropdownMenuItem(
                    text = { Text("Play Next") },
                    leadingIcon = { Icon(Icons.Default.QueuePlayNext, contentDescription = null) },
                    onClick = {
                        showMenu = false
                        onPlayNext()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Add to Queue") },
                    leadingIcon = { Icon(Icons.Default.PlaylistAdd, contentDescription = null) },
                    onClick = {
                        showMenu = false
                        onAddToQueue()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Add to Playlist") },
                    leadingIcon = { Icon(Icons.Default.QueueMusic, contentDescription = null) },
                    onClick = {
                        showMenu = false
                        onAddToPlaylist()
                    }
                )
                if (!song.isDownloaded) {
                    DropdownMenuItem(
                        text = { Text("Download Offline") },
                        leadingIcon = { Icon(Icons.Default.Download, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            onDownload()
                        }
                    )
                }
                if (onDelete != null) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                    DropdownMenuItem(
                        text = { Text("Remove from Library", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
