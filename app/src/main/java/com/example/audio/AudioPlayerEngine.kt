package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.example.data.model.SongEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class RepeatMode {
    OFF,
    REPEAT_ALL,
    REPEAT_ONE
}

data class PlaybackState(
    val currentSong: SongEntity? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isShuffle: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val queue: List<SongEntity> = emptyList(),
    val queueIndex: Int = 0,
    val sleepTimerSecondsRemaining: Int? = null,
    val visualizerWave: List<Float> = listOf(0.3f, 0.6f, 0.9f, 0.4f, 0.8f, 0.5f, 0.7f, 0.3f),
    val playbackError: String? = null,
    val isOnlineStream: Boolean = false
)

class AudioPlayerEngine(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    init {
        startProgressTicker()
    }

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val player = mediaPlayer
                val current = _playbackState.value.currentSong
                if (player != null && _playbackState.value.isPlaying && !_playbackState.value.isBuffering) {
                    val pos = runCatching { player.currentPosition.toLong() }.getOrDefault(0L)
                    val dur = if (current != null && current.durationMs > 0) current.durationMs else runCatching { player.duration.toLong() }.getOrDefault(1000L)

                    // Dynamic soundwave visualizer animation
                    val wave = (0..15).map { index ->
                        val base = ((pos / 300.0 + index * 0.38) % 1.0).toFloat()
                        0.2f + 0.8f * kotlin.math.abs(kotlin.math.sin(base * Math.PI)).toFloat()
                    }

                    _playbackState.value = _playbackState.value.copy(
                        currentPositionMs = pos,
                        durationMs = dur,
                        visualizerWave = wave,
                        playbackError = null
                    )
                } else if (_playbackState.value.isPlaying && current != null && !_playbackState.value.isBuffering) {
                    // Fallback simulated progress for mock stream/offline fallback
                    val newPos = _playbackState.value.currentPositionMs + 500L
                    if (newPos >= current.durationMs) {
                        onTrackFinished()
                    } else {
                        val wave = (0..15).map { index ->
                            val base = ((newPos / 300.0 + index * 0.38) % 1.0).toFloat()
                            0.2f + 0.8f * kotlin.math.abs(kotlin.math.sin(base * Math.PI)).toFloat()
                        }
                        _playbackState.value = _playbackState.value.copy(
                            currentPositionMs = newPos,
                            durationMs = current.durationMs,
                            visualizerWave = wave,
                            playbackError = null
                        )
                    }
                }
                delay(250L)
            }
        }
    }

    fun playTrack(song: SongEntity, newQueue: List<SongEntity>? = null) {
        val queue = newQueue ?: if (_playbackState.value.queue.contains(song)) _playbackState.value.queue else listOf(song)
        val index = queue.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        val isOnline = song.uriString.startsWith("http://") || song.uriString.startsWith("https://")

        stopCurrentMedia()

        _playbackState.value = _playbackState.value.copy(
            currentSong = song,
            isPlaying = true,
            isBuffering = isOnline,
            currentPositionMs = 0L,
            durationMs = song.durationMs,
            queue = queue,
            queueIndex = index,
            playbackError = null,
            isOnlineStream = isOnline
        )

        // Initialize Android MediaPlayer with audio attributes & buffering listeners
        runCatching {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                if (isOnline) {
                    setDataSource(song.uriString)
                    setOnPreparedListener { mp ->
                        _playbackState.value = _playbackState.value.copy(isBuffering = false)
                        mp.start()
                    }
                    setOnBufferingUpdateListener { _, percent ->
                        // Buffering progress
                    }
                    setOnErrorListener { _, what, extra ->
                        _playbackState.value = _playbackState.value.copy(
                            isBuffering = false,
                            playbackError = "Network error loading stream ($what/$extra)"
                        )
                        true
                    }
                    prepareAsync()
                } else if (song.uriString.startsWith("content://") || song.uriString.startsWith("file://")) {
                    setDataSource(context, Uri.parse(song.uriString))
                    prepare()
                    start()
                    _playbackState.value = _playbackState.value.copy(isBuffering = false)
                } else {
                    // Fallback to async mock simulation if non-existent file path
                    _playbackState.value = _playbackState.value.copy(isBuffering = false)
                }

                setOnCompletionListener {
                    onTrackFinished()
                }
            }
            mediaPlayer = player
        }.onFailure {
            _playbackState.value = _playbackState.value.copy(isBuffering = false)
        }
    }

    fun togglePlayPause() {
        val current = _playbackState.value.currentSong ?: return
        val isCurrentlyPlaying = _playbackState.value.isPlaying

        if (isCurrentlyPlaying) {
            mediaPlayer?.pause()
            _playbackState.value = _playbackState.value.copy(isPlaying = false)
        } else {
            mediaPlayer?.start()
            _playbackState.value = _playbackState.value.copy(isPlaying = true)
        }
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.let {
            runCatching { it.seekTo(positionMs.toInt()) }
        }
        _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
    }

    fun skipToNext() {
        val q = _playbackState.value.queue
        if (q.isEmpty()) return

        val nextIndex = if (_playbackState.value.isShuffle) {
            (0 until q.size).random()
        } else {
            (_playbackState.value.queueIndex + 1) % q.size
        }

        playTrack(q[nextIndex], q)
    }

    fun skipToPrevious() {
        val q = _playbackState.value.queue
        if (q.isEmpty()) return

        if (_playbackState.value.currentPositionMs > 3000L) {
            seekTo(0L)
            return
        }

        val prevIndex = if (_playbackState.value.queueIndex - 1 < 0) {
            q.size - 1
        } else {
            _playbackState.value.queueIndex - 1
        }

        playTrack(q[prevIndex], q)
    }

    private fun onTrackFinished() {
        when (_playbackState.value.repeatMode) {
            RepeatMode.REPEAT_ONE -> {
                seekTo(0L)
                mediaPlayer?.start()
                _playbackState.value = _playbackState.value.copy(isPlaying = true)
            }
            RepeatMode.REPEAT_ALL -> {
                skipToNext()
            }
            RepeatMode.OFF -> {
                val q = _playbackState.value.queue
                if (_playbackState.value.queueIndex < q.size - 1) {
                    skipToNext()
                } else {
                    _playbackState.value = _playbackState.value.copy(isPlaying = false, currentPositionMs = 0L)
                }
            }
        }
    }

    fun toggleShuffle() {
        _playbackState.value = _playbackState.value.copy(isShuffle = !_playbackState.value.isShuffle)
    }

    fun cycleRepeatMode() {
        val nextMode = when (_playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.REPEAT_ALL
            RepeatMode.REPEAT_ALL -> RepeatMode.REPEAT_ONE
            RepeatMode.REPEAT_ONE -> RepeatMode.OFF
        }
        _playbackState.value = _playbackState.value.copy(repeatMode = nextMode)
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        if (minutes == null || minutes <= 0) {
            _playbackState.value = _playbackState.value.copy(sleepTimerSecondsRemaining = null)
            return
        }

        val totalSeconds = minutes * 60
        _playbackState.value = _playbackState.value.copy(sleepTimerSecondsRemaining = totalSeconds)

        sleepTimerJob = scope.launch {
            var remaining = totalSeconds
            while (remaining > 0) {
                delay(1000L)
                remaining -= 1
                _playbackState.value = _playbackState.value.copy(sleepTimerSecondsRemaining = remaining)
            }
            if (_playbackState.value.isPlaying) {
                togglePlayPause()
            }
            _playbackState.value = _playbackState.value.copy(sleepTimerSecondsRemaining = null)
        }
    }

    fun addToQueue(song: SongEntity) {
        val updated = _playbackState.value.queue + song
        _playbackState.value = _playbackState.value.copy(queue = updated)
    }

    fun playNext(song: SongEntity) {
        val currentQ = _playbackState.value.queue.toMutableList()
        val insertIndex = (_playbackState.value.queueIndex + 1).coerceAtMost(currentQ.size)
        currentQ.add(insertIndex, song)
        _playbackState.value = _playbackState.value.copy(queue = currentQ)
    }

    fun retryPlayback() {
        _playbackState.value.currentSong?.let { song ->
            playTrack(song, _playbackState.value.queue)
        }
    }

    private fun stopCurrentMedia() {
        mediaPlayer?.let {
            runCatching {
                it.stop()
                it.release()
            }
        }
        mediaPlayer = null
    }

    fun release() {
        progressJob?.cancel()
        sleepTimerJob?.cancel()
        stopCurrentMedia()
    }
}
