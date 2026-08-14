package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.WallpaperScope
import com.example.ui.components.*
import com.example.ui.screens.*

@Composable
fun NovatuneApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Local Data
    val allSongs by viewModel.allSongs.collectAsStateWithLifecycle()
    val favoriteSongs by viewModel.favoriteSongs.collectAsStateWithLifecycle()
    val downloadedSongs by viewModel.downloadedSongs.collectAsStateWithLifecycle()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsStateWithLifecycle()
    val recentlyAdded by viewModel.recentlyAdded.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()

    // Online & Download Manager Data
    val activeDownloads by viewModel.activeDownloads.collectAsStateWithLifecycle()
    val storageInfo by viewModel.storageInfo.collectAsStateWithLifecycle()
    val featuredOnlineTracks by viewModel.featuredOnlineTracks.collectAsStateWithLifecycle()
    val curatedOnlinePlaylists by viewModel.curatedOnlinePlaylists.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchFilter by viewModel.searchFilter.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()

    // Preferences & Settings
    val wifiOnlyDownloads by viewModel.wifiOnlyDownloads.collectAsStateWithLifecycle()
    val autoDownloadFavorites by viewModel.autoDownloadFavorites.collectAsStateWithLifecycle()
    val streamingQuality by viewModel.streamingQuality.collectAsStateWithLifecycle()
    val downloadQuality by viewModel.downloadQuality.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val wallpaperSettings by viewModel.wallpaperSettings.collectAsStateWithLifecycle()
    val hasSeenOnboarding by viewModel.hasSeenOnboarding.collectAsStateWithLifecycle()

    // Navigation & Dialog States
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val isNowPlayingExpanded by viewModel.isNowPlayingExpanded.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedPlaylistDetail by viewModel.selectedPlaylistDetail.collectAsStateWithLifecycle()
    val songForAddToPlaylist by viewModel.songForAddToPlaylist.collectAsStateWithLifecycle()
    val showCreatePlaylistDialog by viewModel.showCreatePlaylistDialog.collectAsStateWithLifecycle()
    val showSleepTimerDialog by viewModel.showSleepTimerDialog.collectAsStateWithLifecycle()
    val selectedSongDetails by viewModel.selectedSongDetails.collectAsStateWithLifecycle()
    val reportingTrackTitle by viewModel.reportingTrackTitle.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    var showWelcomeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(hasSeenOnboarding) {
        if (!hasSeenOnboarding) {
            showWelcomeDialog = true
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Permission Launcher for Local Audio Files
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.rescanLibrary()
        }
    }

    LaunchedEffect(Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(permission)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // App-Wide Live Dynamic Wallpaper Layer
        if (wallpaperSettings.scope == WallpaperScope.APP_WIDE) {
            WallpaperBackground(settings = wallpaperSettings)
        }

        // Main Screen Content & Navigation
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = {
                Column {
                    // Mini Player (Only visible when a song is loaded and not on full Now Playing)
                    if (!isNowPlayingExpanded && playbackState.currentSong != null) {
                        MiniPlayer(
                            playbackState = playbackState,
                            onExpandNowPlaying = { viewModel.setNowPlayingExpanded(true) },
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onSkipNext = { viewModel.skipToNext() }
                        )
                    }

                    // Navigation Bar
                    NovatuneBottomBar(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            if (selectedPlaylistDetail != null && tab != NovatuneTab.PLAYLISTS) {
                                viewModel.openPlaylistDetail(null)
                            }
                            viewModel.selectTab(tab)
                        }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedTab) {
                    NovatuneTab.HOME -> {
                        HomeScreen(
                            recentlyPlayed = recentlyPlayed,
                            recentlyAdded = recentlyAdded,
                            favoriteSongs = favoriteSongs,
                            downloadedSongs = downloadedSongs,
                            playlists = playlists,
                            featuredOnlineTracks = featuredOnlineTracks,
                            curatedOnlinePlaylists = curatedOnlinePlaylists,
                            playbackState = playbackState,
                            onNavigateTab = { viewModel.selectTab(it) },
                            onSongClick = { song, queue -> viewModel.playSong(song, queue) },
                            onPlayOnlineTrack = { viewModel.playOnlineTrack(it) },
                            onDownloadOnlineTrack = { viewModel.startDownloadOnlineTrack(it) },
                            onShowTrackDetails = { viewModel.showOnlineTrackDetails(it) },
                            onOpenOnlinePlaylist = { viewModel.openOnlinePlaylist(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onPlayNext = { viewModel.playNext(it) },
                            onAddToQueue = { viewModel.addToQueue(it) },
                            onAddToPlaylist = { viewModel.requestAddToPlaylist(it) },
                            onDownload = { viewModel.startDownload(it) },
                            onOpenPlaylist = {
                                viewModel.openPlaylistDetail(it)
                                viewModel.selectTab(NovatuneTab.PLAYLISTS)
                            },
                            onCreatePlaylist = { viewModel.showCreatePlaylistDialog(true) }
                        )
                    }

                    NovatuneTab.SEARCH -> {
                        SearchScreen(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            selectedCategory = searchFilter,
                            onCategoryChange = { viewModel.setSearchFilter(it) },
                            searchResults = searchResults,
                            isLoading = isSearching,
                            recentSearches = recentSearches,
                            onSelectRecentSearch = { viewModel.setSearchQuery(it) },
                            onRemoveRecentSearch = { viewModel.removeRecentSearch(it) },
                            onClearRecentSearches = { viewModel.clearRecentSearches() },
                            onPlayTrack = { viewModel.playOnlineTrack(it) },
                            onDownloadTrack = { viewModel.startDownloadOnlineTrack(it) },
                            onToggleFavorite = { viewModel.toggleFavoriteOnlineTrack(it) },
                            onAddToPlaylist = { viewModel.requestAddToPlaylistOnlineTrack(it) },
                            onShowTrackDetails = { viewModel.showOnlineTrackDetails(it) },
                            onReportContent = { viewModel.requestReportContent(it.title) },
                            onPlayArtistTracks = { viewModel.playArtistTracks(it) },
                            onPlayAlbumTracks = { viewModel.playAlbumTracks(it) },
                            onOpenPlaylist = { viewModel.openOnlinePlaylist(it) },
                            onRetry = { viewModel.setSearchQuery("") }
                        )
                    }

                    NovatuneTab.SONGS -> {
                        SongsScreen(
                            songs = allSongs,
                            playbackState = playbackState,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onSongClick = { song, queue -> viewModel.playSong(song, queue) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onPlayNext = { viewModel.playNext(it) },
                            onAddToQueue = { viewModel.addToQueue(it) },
                            onAddToPlaylist = { viewModel.requestAddToPlaylist(it) },
                            onDownload = { viewModel.startDownload(it) },
                            onDeleteSong = { viewModel.deleteSong(it) },
                            onRescanLibrary = { viewModel.rescanLibrary() }
                        )
                    }

                    NovatuneTab.PLAYLISTS -> {
                        PlaylistsScreen(
                            playlists = playlists,
                            selectedPlaylist = selectedPlaylistDetail,
                            playbackState = playbackState,
                            getPlaylistSongs = { viewModel.getSongsForPlaylist(it) },
                            onOpenPlaylist = { viewModel.openPlaylistDetail(it) },
                            onCreatePlaylist = { viewModel.showCreatePlaylistDialog(true) },
                            onDeletePlaylist = { viewModel.deletePlaylist(it) },
                            onPlaySong = { song, queue -> viewModel.playSong(song, queue) },
                            onPlayAll = { songs ->
                                if (songs.isNotEmpty()) viewModel.playSong(songs.first(), songs)
                            },
                            onShuffleAll = { songs ->
                                if (songs.isNotEmpty()) {
                                    val shuffled = songs.shuffled()
                                    viewModel.playSong(shuffled.first(), shuffled)
                                }
                            },
                            onRemoveSongFromPlaylist = { pId, sId -> viewModel.removeSongFromPlaylist(pId, sId) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onPlayNext = { viewModel.playNext(it) },
                            onAddToQueue = { viewModel.addToQueue(it) },
                            onAddToPlaylist = { viewModel.requestAddToPlaylist(it) },
                            onDownload = { viewModel.startDownload(it) }
                        )
                    }

                    NovatuneTab.DOWNLOADS -> {
                        DownloadsScreen(
                            downloads = downloads,
                            activeDownloads = activeDownloads,
                            downloadedSongs = downloadedSongs,
                            storageInfo = storageInfo,
                            playbackState = playbackState,
                            onSongClick = { song, queue -> viewModel.playSong(song, queue) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onPlayNext = { viewModel.playNext(it) },
                            onAddToQueue = { viewModel.addToQueue(it) },
                            onAddToPlaylist = { viewModel.requestAddToPlaylist(it) },
                            onPauseDownload = { viewModel.pauseDownload(it) },
                            onResumeDownload = { viewModel.resumeDownload(it) },
                            onCancelDownload = { viewModel.cancelDownload(it) },
                            onDeleteDownload = { viewModel.deleteDownload(it) },
                            onRescanDownloads = { viewModel.rescanLibrary() }
                        )
                    }

                    NovatuneTab.SETTINGS -> {
                        SettingsScreen(
                            wallpaperSettings = wallpaperSettings,
                            onUpdateWallpaperSettings = { viewModel.updateWallpaperSettings(it) },
                            onRescanLibrary = { viewModel.rescanLibrary() },
                            onExportBackup = { viewModel.exportBackup(it) },
                            wifiOnlyDownloads = wifiOnlyDownloads,
                            autoDownloadFavorites = autoDownloadFavorites,
                            streamingQuality = streamingQuality,
                            downloadQuality = downloadQuality,
                            onToggleWifiOnlyDownloads = { viewModel.toggleWifiOnlyDownloads(it) },
                            onToggleAutoDownloadFavorites = { viewModel.toggleAutoDownloadFavorites(it) },
                            onSelectStreamingQuality = { viewModel.setStreamingQuality(it) },
                            onSelectDownloadQuality = { viewModel.setDownloadQuality(it) }
                        )
                    }
                }
            }
        }

        // Full Screen Animated Now Playing Sheet
        AnimatedVisibility(
            visible = isNowPlayingExpanded,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (wallpaperSettings.scope == WallpaperScope.NOW_PLAYING_ONLY) {
                    WallpaperBackground(settings = wallpaperSettings)
                }

                NowPlayingSheet(
                    playbackState = playbackState,
                    onCollapse = { viewModel.setNowPlayingExpanded(false) },
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onSeekTo = { viewModel.seekTo(it) },
                    onSkipNext = { viewModel.skipToNext() },
                    onSkipPrevious = { viewModel.skipToPrevious() },
                    onToggleShuffle = { viewModel.toggleShuffle() },
                    onCycleRepeat = { viewModel.cycleRepeatMode() },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onAddToPlaylist = { viewModel.requestAddToPlaylist(it) },
                    onDownloadSong = { viewModel.startDownload(it) },
                    onShowSongDetails = { viewModel.showSongDetails(it) },
                    onOpenSleepTimer = { viewModel.showSleepTimerDialog(true) },
                    onSelectQueueSong = { viewModel.playSong(it, playbackState.queue) },
                    onRetryPlayback = { viewModel.retryPlayback() }
                )
            }
        }

        // Dialogs
        if (showCreatePlaylistDialog) {
            CreatePlaylistDialog(
                onDismiss = { viewModel.showCreatePlaylistDialog(false) },
                onCreate = { title, desc -> viewModel.createPlaylist(title, desc) }
            )
        }

        songForAddToPlaylist?.let { song ->
            AddToPlaylistDialog(
                song = song,
                playlists = playlists,
                onDismiss = { viewModel.dismissAddToPlaylist() },
                onSelectPlaylist = { p -> viewModel.addSongToPlaylist(p, song) },
                onCreateNewPlaylist = {
                    viewModel.showCreatePlaylistDialog(true)
                }
            )
        }

        if (showSleepTimerDialog) {
            SleepTimerDialog(
                activeMinutesRemaining = playbackState.sleepTimerSecondsRemaining,
                onDismiss = { viewModel.showSleepTimerDialog(false) },
                onSetTimer = { mins -> viewModel.setSleepTimer(mins) }
            )
        }

        if (showWelcomeDialog) {
            WelcomeDialog(
                onDismiss = {
                    showWelcomeDialog = false
                    viewModel.completeOnboarding()
                },
                onGetStarted = {
                    showWelcomeDialog = false
                    viewModel.completeOnboarding()
                }
            )
        }

        // Song Details Dialog
        selectedSongDetails?.let { (song, onlineTrack) ->
            SongDetailsDialog(
                song = song,
                onlineTrack = onlineTrack,
                onDismiss = { viewModel.dismissSongDetails() },
                onPlay = {
                    if (onlineTrack != null) viewModel.playOnlineTrack(onlineTrack)
                    else if (song != null) viewModel.playSong(song)
                },
                onDownload = {
                    if (onlineTrack != null) viewModel.startDownloadOnlineTrack(onlineTrack)
                    else if (song != null) viewModel.startDownload(song)
                },
                onToggleFavorite = {
                    if (onlineTrack != null) viewModel.toggleFavoriteOnlineTrack(onlineTrack)
                    else if (song != null) viewModel.toggleFavorite(song)
                },
                onAddToPlaylist = {
                    if (onlineTrack != null) viewModel.requestAddToPlaylistOnlineTrack(onlineTrack)
                    else if (song != null) viewModel.requestAddToPlaylist(song)
                },
                onReportContent = {
                    val title = onlineTrack?.title ?: song?.title ?: "Track"
                    viewModel.requestReportContent(title)
                }
            )
        }

        // Report Content Dialog
        reportingTrackTitle?.let { trackTitle ->
            ReportContentDialog(
                itemTitle = trackTitle,
                onDismiss = { viewModel.dismissReportContent() },
                onSubmitReport = { reason, details ->
                    viewModel.submitContentReport(reason, details)
                }
            )
        }
    }
}
