package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerEngine
import com.example.audio.PlaybackState
import com.example.data.local.PreferencesManager
import com.example.data.model.*
import com.example.data.repository.MusicRepository
import com.example.ui.components.NovatuneTab
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MusicRepository(application)
    private val audioPlayerEngine = AudioPlayerEngine(application)
    private val preferencesManager = PreferencesManager(application)

    // Local library and database flows
    val allSongs: StateFlow<List<SongEntity>> = repository.allSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSongs: StateFlow<List<SongEntity>> = repository.favoriteSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadedSongs: StateFlow<List<SongEntity>> = repository.downloadedSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyPlayed: StateFlow<List<SongEntity>> = repository.recentlyPlayed
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyAdded: StateFlow<List<SongEntity>> = repository.recentlyAdded
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playlists: StateFlow<List<PlaylistEntity>> = repository.allPlaylists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloads: StateFlow<List<DownloadEntity>> = repository.allDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Downloads & Storage Stats
    val activeDownloads: StateFlow<Map<String, DownloadProgressItem>> = repository.downloadManager.activeDownloads
    val storageInfo: StateFlow<StorageInfo> = repository.downloadManager.storageInfo

    // Download & Streaming Preferences
    val wifiOnlyDownloads: StateFlow<Boolean> = preferencesManager.wifiOnlyDownloads
    val autoDownloadFavorites: StateFlow<Boolean> = preferencesManager.autoDownloadFavorites
    val streamingQuality: StateFlow<String> = preferencesManager.streamingQuality
    val downloadQuality: StateFlow<String> = preferencesManager.downloadQuality
    val recentSearches: StateFlow<List<String>> = preferencesManager.recentSearches

    // Audio Engine State
    val playbackState: StateFlow<PlaybackState> = audioPlayerEngine.playbackState

    // Visual / Wallpaper State
    val wallpaperSettings: StateFlow<WallpaperSettings> = preferencesManager.wallpaperSettings
    val hasSeenOnboarding: StateFlow<Boolean> = preferencesManager.hasSeenOnboarding

    // UI Nav & Dialog States
    private val _selectedTab = MutableStateFlow(NovatuneTab.HOME)
    val selectedTab: StateFlow<NovatuneTab> = _selectedTab.asStateFlow()

    private val _isNowPlayingExpanded = MutableStateFlow(false)
    val isNowPlayingExpanded: StateFlow<Boolean> = _isNowPlayingExpanded.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchFilter = MutableStateFlow(SearchFilterCategory.ALL)
    val searchFilter: StateFlow<SearchFilterCategory> = _searchFilter.asStateFlow()

    private val _searchResults = MutableStateFlow(OnlineSearchResult())
    val searchResults: StateFlow<OnlineSearchResult> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _featuredOnlineTracks = MutableStateFlow<List<OnlineTrack>>(emptyList())
    val featuredOnlineTracks: StateFlow<List<OnlineTrack>> = _featuredOnlineTracks.asStateFlow()

    private val _curatedOnlinePlaylists = MutableStateFlow<List<OnlinePlaylist>>(emptyList())
    val curatedOnlinePlaylists: StateFlow<List<OnlinePlaylist>> = _curatedOnlinePlaylists.asStateFlow()

    private val _selectedPlaylistDetail = MutableStateFlow<PlaylistEntity?>(null)
    val selectedPlaylistDetail: StateFlow<PlaylistEntity?> = _selectedPlaylistDetail.asStateFlow()

    private val _songForAddToPlaylist = MutableStateFlow<SongEntity?>(null)
    val songForAddToPlaylist: StateFlow<SongEntity?> = _songForAddToPlaylist.asStateFlow()

    private val _showCreatePlaylistDialog = MutableStateFlow(false)
    val showCreatePlaylistDialog: StateFlow<Boolean> = _showCreatePlaylistDialog.asStateFlow()

    private val _showSleepTimerDialog = MutableStateFlow(false)
    val showSleepTimerDialog: StateFlow<Boolean> = _showSleepTimerDialog.asStateFlow()

    private val _selectedSongDetails = MutableStateFlow<Pair<SongEntity?, OnlineTrack?>?>(null)
    val selectedSongDetails: StateFlow<Pair<SongEntity?, OnlineTrack?>?> = _selectedSongDetails.asStateFlow()

    private val _reportingTrackTitle = MutableStateFlow<String?>(null)
    val reportingTrackTitle: StateFlow<String?> = _reportingTrackTitle.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadOnlineCatalogDiscovery()
    }

    private fun loadOnlineCatalogDiscovery() {
        viewModelScope.launch {
            _featuredOnlineTracks.value = repository.getFeaturedOnlineTracks()
            _curatedOnlinePlaylists.value = repository.getCuratedOnlinePlaylists()
        }
    }

    fun selectTab(tab: NovatuneTab) {
        _selectedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        triggerSearch(query, _searchFilter.value)
    }

    fun setSearchFilter(filter: SearchFilterCategory) {
        _searchFilter.value = filter
        triggerSearch(_searchQuery.value, filter)
    }

    private fun triggerSearch(query: String, filter: SearchFilterCategory) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = OnlineSearchResult()
            _isSearching.value = false
            return
        }

        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(200) // Debounce rapid keystrokes
            val results = repository.searchOnlineCatalog(query, filter)
            _searchResults.value = results
            _isSearching.value = false
            preferencesManager.addRecentSearch(query)
        }
    }

    fun removeRecentSearch(query: String) {
        preferencesManager.removeRecentSearch(query)
    }

    fun clearRecentSearches() {
        preferencesManager.clearRecentSearches()
    }

    fun setNowPlayingExpanded(expanded: Boolean) {
        _isNowPlayingExpanded.value = expanded
    }

    fun openPlaylistDetail(playlist: PlaylistEntity?) {
        _selectedPlaylistDetail.value = playlist
    }

    fun getSongsForPlaylist(playlistId: Long): Flow<List<SongEntity>> {
        return repository.getSongsForPlaylist(playlistId)
    }

    fun playSong(song: SongEntity, queue: List<SongEntity>? = null) {
        audioPlayerEngine.playTrack(song, queue)
        viewModelScope.launch {
            repository.recordPlay(song.id)
        }
    }

    fun playOnlineTrack(track: OnlineTrack) {
        val songEntity = track.toSongEntity()
        playSong(songEntity, listOf(songEntity))
    }

    fun playArtistTracks(artist: OnlineArtist) {
        viewModelScope.launch {
            val results = repository.searchOnlineCatalog(artist.name, SearchFilterCategory.SONGS)
            if (results.songs.isNotEmpty()) {
                val songEntities = results.songs.map { it.toSongEntity() }
                playSong(songEntities.first(), songEntities)
                _toastMessage.value = "Playing artist: ${artist.name}"
            }
        }
    }

    fun playAlbumTracks(album: OnlineAlbum) {
        viewModelScope.launch {
            val results = repository.searchOnlineCatalog(album.title, SearchFilterCategory.SONGS)
            if (results.songs.isNotEmpty()) {
                val songEntities = results.songs.map { it.toSongEntity() }
                playSong(songEntities.first(), songEntities)
                _toastMessage.value = "Playing album: ${album.title}"
            }
        }
    }

    fun openOnlinePlaylist(playlist: OnlinePlaylist) {
        viewModelScope.launch {
            val results = repository.searchOnlineCatalog(playlist.genre, SearchFilterCategory.SONGS)
            if (results.songs.isNotEmpty()) {
                val songEntities = results.songs.map { it.toSongEntity() }
                playSong(songEntities.first(), songEntities)
                _toastMessage.value = "Playing playlist: ${playlist.title}"
            }
        }
    }

    fun togglePlayPause() {
        audioPlayerEngine.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        audioPlayerEngine.seekTo(positionMs)
    }

    fun skipToNext() {
        audioPlayerEngine.skipToNext()
    }

    fun skipToPrevious() {
        audioPlayerEngine.skipToPrevious()
    }

    fun toggleShuffle() {
        audioPlayerEngine.toggleShuffle()
    }

    fun cycleRepeatMode() {
        audioPlayerEngine.cycleRepeatMode()
    }

    fun retryPlayback() {
        audioPlayerEngine.retryPlayback()
    }

    fun setSleepTimer(minutes: Int?) {
        audioPlayerEngine.setSleepTimer(minutes)
        _showSleepTimerDialog.value = false
        _toastMessage.value = if (minutes != null) "Sleep timer set for $minutes minutes" else "Sleep timer turned off"
    }

    fun toggleFavorite(song: SongEntity) {
        viewModelScope.launch {
            val nextFav = !song.isFavorite
            repository.toggleFavorite(song.id, song.isFavorite)
            if (nextFav && autoDownloadFavorites.value && !song.isDownloaded) {
                startDownload(song)
            }
        }
    }

    fun toggleFavoriteOnlineTrack(track: OnlineTrack) {
        viewModelScope.launch {
            val song = track.toSongEntity()
            playSong(song)
            repository.toggleFavorite(song.id, false)
            _toastMessage.value = "Added \"${track.title}\" to Favorites"
        }
    }

    fun playNext(song: SongEntity) {
        audioPlayerEngine.playNext(song)
        _toastMessage.value = "Playing next: ${song.title}"
    }

    fun addToQueue(song: SongEntity) {
        audioPlayerEngine.addToQueue(song)
        _toastMessage.value = "Added to queue: ${song.title}"
    }

    fun requestAddToPlaylist(song: SongEntity) {
        _songForAddToPlaylist.value = song
    }

    fun requestAddToPlaylistOnlineTrack(track: OnlineTrack) {
        _songForAddToPlaylist.value = track.toSongEntity()
    }

    fun dismissAddToPlaylist() {
        _songForAddToPlaylist.value = null
    }

    fun addSongToPlaylist(playlist: PlaylistEntity, song: SongEntity) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlist.playlistId, song.id)
            _songForAddToPlaylist.value = null
            _toastMessage.value = "Added \"${song.title}\" to \"${playlist.title}\""
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            repository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    fun showCreatePlaylistDialog(show: Boolean) {
        _showCreatePlaylistDialog.value = show
    }

    fun showSleepTimerDialog(show: Boolean) {
        _showSleepTimerDialog.value = show
    }

    fun createPlaylist(title: String, description: String) {
        viewModelScope.launch {
            val newId = repository.createPlaylist(title, description)
            _showCreatePlaylistDialog.value = false
            _songForAddToPlaylist.value?.let { song ->
                repository.addSongToPlaylist(newId, song.id)
                _songForAddToPlaylist.value = null
            }
            _toastMessage.value = "Created playlist \"$title\""
        }
    }

    fun deletePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            repository.deletePlaylist(playlist)
            if (_selectedPlaylistDetail.value?.playlistId == playlist.playlistId) {
                _selectedPlaylistDetail.value = null
            }
            _toastMessage.value = "Deleted playlist \"${playlist.title}\""
        }
    }

    fun startDownload(song: SongEntity) {
        viewModelScope.launch {
            repository.startAuthorizedDownload(song)
            _toastMessage.value = "Saved \"${song.title}\" to Music/Novatune"
        }
    }

    fun startDownloadOnlineTrack(track: OnlineTrack) {
        viewModelScope.launch {
            val res = repository.enqueueDownload(track, wifiOnlyDownloads.value, downloadQuality.value)
            if (res.isSuccess) {
                _toastMessage.value = "Downloading \"${track.title}\" (${track.audioQualityTag})..."
            } else {
                _toastMessage.value = res.exceptionOrNull()?.message ?: "Download failed"
            }
        }
    }

    fun pauseDownload(downloadId: String) {
        repository.downloadManager.pauseDownload(downloadId)
        _toastMessage.value = "Download paused"
    }

    fun resumeDownload(item: DownloadProgressItem) {
        val track = OnlineTrack(
            id = item.downloadId,
            title = item.title,
            artist = item.artist,
            album = "Authorized Release",
            durationMs = 180_000L,
            coverUrl = "",
            streamUrl = item.sourceUrl,
            downloadUrl = item.sourceUrl,
            audioQualityTag = item.audioQuality,
            licenseType = "CC BY-NC 4.0",
            genre = "Music"
        )
        viewModelScope.launch {
            repository.enqueueDownload(track, wifiOnlyDownloads.value, item.audioQuality)
        }
    }

    fun cancelDownload(downloadId: String) {
        repository.downloadManager.cancelDownload(downloadId)
        _toastMessage.value = "Download cancelled"
    }

    fun deleteDownload(download: DownloadEntity) {
        viewModelScope.launch {
            repository.deleteDownload(download)
            _toastMessage.value = "Deleted \"${download.title}\" from device storage"
        }
    }

    fun deleteSong(song: SongEntity) {
        viewModelScope.launch {
            repository.deleteSong(song.id)
            _toastMessage.value = "Removed \"${song.title}\" from library"
        }
    }

    fun showSongDetails(song: SongEntity) {
        _selectedSongDetails.value = Pair(song, null)
    }

    fun showOnlineTrackDetails(track: OnlineTrack) {
        _selectedSongDetails.value = Pair(null, track)
    }

    fun dismissSongDetails() {
        _selectedSongDetails.value = null
    }

    fun requestReportContent(title: String) {
        _reportingTrackTitle.value = title
    }

    fun dismissReportContent() {
        _reportingTrackTitle.value = null
    }

    fun submitContentReport(reason: String, details: String) {
        val title = _reportingTrackTitle.value ?: "Track"
        _reportingTrackTitle.value = null
        _toastMessage.value = "Report submitted for \"$title\": $reason. We will verify copyright and licenses immediately."
    }

    fun toggleWifiOnlyDownloads(enabled: Boolean) {
        preferencesManager.setWifiOnlyDownloads(enabled)
        _toastMessage.value = if (enabled) "Downloads set to Wi-Fi only" else "Downloads allowed over cellular"
    }

    fun toggleAutoDownloadFavorites(enabled: Boolean) {
        preferencesManager.setAutoDownloadFavorites(enabled)
        _toastMessage.value = if (enabled) "Auto-downloading authorized favorites" else "Auto-download favorites disabled"
    }

    fun setStreamingQuality(quality: String) {
        preferencesManager.setStreamingQuality(quality)
        _toastMessage.value = "Streaming quality set to $quality"
    }

    fun setDownloadQuality(quality: String) {
        preferencesManager.setDownloadQuality(quality)
        _toastMessage.value = "Download quality set to $quality"
    }

    fun rescanLibrary() {
        viewModelScope.launch {
            val count = repository.rescanDeviceMedia()
            _toastMessage.value = if (count > 0) "Found $count local songs on device" else "Library is up to date"
        }
    }

    fun updateWallpaperSettings(settings: WallpaperSettings) {
        preferencesManager.updateWallpaperSettings(settings)
    }

    fun completeOnboarding() {
        preferencesManager.setHasSeenOnboarding(true)
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun exportBackup(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportPlaylistsJson()
            val backup = preferencesManager.exportBackupJson(json, allSongs.value.size)
            onResult(backup)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerEngine.release()
    }
}
