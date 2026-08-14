package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.OnlineTrack
import com.example.data.model.SongEntity
import com.example.ui.theme.AccentVioletPrimary

@Composable
fun SongDetailsDialog(
    song: SongEntity?,
    onlineTrack: OnlineTrack? = null,
    onDismiss: () -> Unit,
    onPlay: () -> Unit,
    onDownload: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onReportContent: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (song == null && onlineTrack == null) return

    val title = onlineTrack?.title ?: song?.title ?: "Unknown Track"
    val artist = onlineTrack?.artist ?: song?.artist ?: "Unknown Artist"
    val album = onlineTrack?.album ?: song?.album ?: "Unknown Album"
    val durationMs = onlineTrack?.durationMs ?: song?.durationMs ?: 0L
    val qualityTag = onlineTrack?.audioQualityTag ?: song?.qualityTag ?: "320kbps MP3"
    val license = onlineTrack?.licenseType ?: "Local Media File"
    val isDownloadPermitted = onlineTrack?.isDownloadPermitted ?: (song?.isDownloaded == false)
    val coverUrl = onlineTrack?.coverUrl
    val genre = onlineTrack?.genre ?: "Music"
    val lyrics = onlineTrack?.lyrics ?: song?.lyrics

    val minutes = durationMs / 1000 / 60
    val seconds = (durationMs / 1000) % 60
    val durationStr = String.format("%d:%02d", minutes, seconds)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp)),
            color = Color(0xFF1E1C22),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Artwork Header
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF4A2B5E), Color(0xFF1A3C5E))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!coverUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = coverUrl,
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = AccentVioletPrimary,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "$artist • $album",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Metadata Badges Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgeChip(text = durationStr, icon = Icons.Outlined.Timer)
                    Spacer(modifier = Modifier.width(8.dp))
                    BadgeChip(text = qualityTag, icon = Icons.Outlined.GraphicEq)
                    Spacer(modifier = Modifier.width(8.dp))
                    BadgeChip(text = genre, icon = Icons.Outlined.Category)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Licensing verification card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.04f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "License",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Authorized License",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = license,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                if (!lyrics.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.03f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Lyrics Preview",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentVioletPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lyrics.take(180) + if (lyrics.length > 180) "..." else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.75f),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            onPlay()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("dialog_play_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentVioletPrimary,
                            contentColor = Color(0xFF1C1B1F)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Stream / Play", fontWeight = FontWeight.Bold)
                    }

                    if (isDownloadPermitted) {
                        OutlinedButton(
                            onClick = {
                                onDownload()
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("dialog_download_button"),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = {
                        onToggleFavorite()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Favorite", color = Color.White.copy(alpha = 0.8f))
                    }

                    TextButton(onClick = {
                        onAddToPlaylist()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.PlaylistAdd, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Playlist", color = Color.White.copy(alpha = 0.8f))
                    }

                    TextButton(onClick = {
                        onDismiss()
                        onReportContent()
                    }) {
                        Icon(Icons.Outlined.ReportProblem, contentDescription = null, tint = Color(0xFFFF8A80))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Report", color = Color(0xFFFF8A80))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = Color.White.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
private fun BadgeChip(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.06f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }
    }
}
