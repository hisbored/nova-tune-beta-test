package com.example.data.repository

import android.content.Context
import com.example.data.local.NovatuneDatabase
import com.example.data.model.*
import com.example.data.remote.AuthorizedMusicCatalog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicRepository(
    private val context: Context,
    private val database: NovatuneDatabase = NovatuneDatabase.getDatabase(context)
) {
    private val songDao = database.songDao()
    private val playlistDao = database.playlistDao()
    private val downloadDao = database.downloadDao()
    val downloadManager = DownloadManagerService(context, database)

    val allSongs: Flow<List<SongEntity>> = songDao.getAllSongs()
    val favoriteSongs: Flow<List<SongEntity>> = songDao.getFavoriteSongs()
    val downloadedSongs: Flow<List<SongEntity>> = songDao.getDownloadedSongs()
    val recentlyPlayed: Flow<List<SongEntity>> = songDao.getRecentlyPlayedSongs()
    val recentlyAdded: Flow<List<SongEntity>> = songDao.getRecentlyAddedSongs()
    val allPlaylists: Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()
    val allDownloads: Flow<List<DownloadEntity>> = downloadDao.getAllDownloads()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDefaultDataIfEmpty()
        }
    }

    private suspend fun seedDefaultDataIfEmpty() {
        val count = songDao.getSongCount()
        if (count == 0) {
            // Seed sample tracks
            val sampleTracks = MediaScannerHelper.getSampleAuthorizedTracks()
            songDao.insertSongs(sampleTracks)

            // Seed default playlists
            val favPlaylistId = playlistDao.insertPlaylist(
                PlaylistEntity(
                    title = "My Favorites",
                    description = "Top rated and beloved tracks",
                    coverGradientStart = 0xFF4A2B5EL,
                    coverGradientEnd = 0xFF1A3C5EL,
                    coverIconName = "favorite"
                )
            )

            val energyPlaylistId = playlistDao.insertPlaylist(
                PlaylistEntity(
                    title = "Energy Boost",
                    description = "High tempo synthesizer and electronic beats",
                    coverGradientStart = 0xFF9A3412L,
                    coverGradientEnd = 0xFF4C0519L,
                    coverIconName = "bolt"
                )
            )

            val ambientPlaylistId = playlistDao.insertPlaylist(
                PlaylistEntity(
                    title = "Deep Focus & Study",
                    description = "Atmospheric soundscapes for concentration",
                    coverGradientStart = 0xFF064E3BL,
                    coverGradientEnd = 0xFF0F172AL,
                    coverIconName = "spa"
                )
            )

            // Add some tracks to playlists
            playlistDao.insertPlaylistSongCrossRef(PlaylistSongCrossRef(favPlaylistId, 1001L, 0))
            playlistDao.insertPlaylistSongCrossRef(PlaylistSongCrossRef(favPlaylistId, 1002L, 1))
            playlistDao.insertPlaylistSongCrossRef(PlaylistSongCrossRef(favPlaylistId, 1004L, 2))

            playlistDao.insertPlaylistSongCrossRef(PlaylistSongCrossRef(energyPlaylistId, 1003L, 0))
            playlistDao.insertPlaylistSongCrossRef(PlaylistSongCrossRef(energyPlaylistId, 1005L, 1))

            playlistDao.insertPlaylistSongCrossRef(PlaylistSongCrossRef(ambientPlaylistId, 1002L, 0))
            playlistDao.insertPlaylistSongCrossRef(PlaylistSongCrossRef(ambientPlaylistId, 1006L, 1))

            // Seed initial download items
            downloadDao.insertDownload(
                DownloadEntity(
                    downloadId = "dl_1001",
                    title = "Lunar Echoes",
                    artist = "Nova Collective",
                    album = "Celestial Resonance",
                    durationMs = 214_000L,
                    sourceUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
                    localFilePath = "Music/Novatune/Lunar_Echoes.mp3",
                    status = "COMPLETED",
                    progressPercent = 100,
                    sizeBytes = 9_450_000L,
                    audioQuality = "320kbps MP3",
                    licenseTag = "CC BY-NC 4.0"
                )
            )
            downloadDao.insertDownload(
                DownloadEntity(
                    downloadId = "dl_1002",
                    title = "Drift",
                    artist = "Sola",
                    album = "Atmospheric Horizons",
                    durationMs = 185_000L,
                    sourceUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
                    localFilePath = "Music/Novatune/Drift.mp3",
                    status = "COMPLETED",
                    progressPercent = 100,
                    sizeBytes = 7_210_000L,
                    audioQuality = "FLAC Lossless",
                    licenseTag = "CC0 Public Domain"
                )
            )
        }
    }

    suspend fun searchOnlineCatalog(query: String, filter: SearchFilterCategory): OnlineSearchResult {
        return withContext(Dispatchers.IO) {
            AuthorizedMusicCatalog.searchCatalog(query, filter)
        }
    }

    suspend fun getFeaturedOnlineTracks(): List<OnlineTrack> = withContext(Dispatchers.IO) {
        AuthorizedMusicCatalog.getFeaturedTracks()
    }

    suspend fun getRecommendedOnlineTracks(): List<OnlineTrack> = withContext(Dispatchers.IO) {
        AuthorizedMusicCatalog.getRecommendedTracks()
    }

    suspend fun getCuratedOnlinePlaylists(): List<OnlinePlaylist> = withContext(Dispatchers.IO) {
        AuthorizedMusicCatalog.getCuratedPlaylists()
    }

    suspend fun enqueueDownload(track: OnlineTrack, wifiOnly: Boolean, quality: String): Result<String> {
        return downloadManager.enqueueAuthorizedDownload(track, wifiOnly, quality)
    }

    suspend fun rescanDeviceMedia(): Int = withContext(Dispatchers.IO) {
        val scanned = MediaScannerHelper.scanDeviceMusic(context)
        if (scanned.isNotEmpty()) {
            songDao.insertSongs(scanned)
        }
        scanned.size
    }

    suspend fun toggleFavorite(songId: Long, currentFav: Boolean) = withContext(Dispatchers.IO) {
        songDao.updateFavorite(songId, !currentFav)
    }

    suspend fun recordPlay(songId: Long) = withContext(Dispatchers.IO) {
        songDao.recordPlay(songId)
    }

    fun getSongsForPlaylist(playlistId: Long): Flow<List<SongEntity>> {
        return playlistDao.getSongsForPlaylist(playlistId)
    }

    suspend fun createPlaylist(title: String, description: String, startGrad: Long = 0xFF4A2B5EL, endGrad: Long = 0xFF1A3C5EL): Long = withContext(Dispatchers.IO) {
        playlistDao.insertPlaylist(
            PlaylistEntity(
                title = title,
                description = description,
                coverGradientStart = startGrad,
                coverGradientEnd = endGrad
            )
        )
    }

    suspend fun deletePlaylist(playlist: PlaylistEntity) = withContext(Dispatchers.IO) {
        playlistDao.clearPlaylistSongs(playlist.playlistId)
        playlistDao.deletePlaylist(playlist)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        playlistDao.insertPlaylistSongCrossRef(
            PlaylistSongCrossRef(
                playlistId = playlistId,
                songId = songId,
                sortOrder = System.currentTimeMillis().toInt()
            )
        )
        val p = playlistDao.getPlaylistById(playlistId)
        if (p != null) {
            playlistDao.updatePlaylist(p.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }

    suspend fun startAuthorizedDownload(song: SongEntity) = withContext(Dispatchers.IO) {
        val download = DownloadEntity(
            downloadId = "dl_${song.id}_${System.currentTimeMillis()}",
            title = song.title,
            artist = song.artist,
            album = song.album,
            durationMs = song.durationMs,
            sourceUrl = "https://novatune.audio/cc/${song.title.lowercase().replace(" ", "_")}.mp3",
            localFilePath = "Music/Novatune/${song.title.replace(" ", "_")}.mp3",
            status = "COMPLETED",
            progressPercent = 100,
            sizeBytes = (song.durationMs * 44).toLong(),
            audioQuality = song.qualityTag,
            licenseTag = "CC BY-NC 4.0"
        )
        downloadDao.insertDownload(download)
        songDao.updateSong(song.copy(isDownloaded = true))
    }

    suspend fun deleteDownload(download: DownloadEntity) = withContext(Dispatchers.IO) {
        downloadManager.deleteDownloadedFile(download)
    }

    suspend fun deleteSong(songId: Long) = withContext(Dispatchers.IO) {
        songDao.deleteSongById(songId)
    }

    suspend fun exportPlaylistsJson(): String = withContext(Dispatchers.IO) {
        val playlists = playlistDao.getAllPlaylists().first()
        val builder = StringBuilder("[")
        playlists.forEachIndexed { index, p ->
            val songs = playlistDao.getSongsForPlaylist(p.playlistId).first()
            val songIds = songs.map { it.id }.joinToString(",")
            builder.append("""{"id":${p.playlistId},"title":"${p.title}","description":"${p.description}","songs":[$songIds]}""")
            if (index < playlists.size - 1) builder.append(",")
        }
        builder.append("]")
        builder.toString()
    }
}
