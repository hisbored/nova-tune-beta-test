package com.example.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.data.model.SongEntity

object MediaScannerHelper {

    fun scanDeviceMusic(context: Context): List<SongEntity> {
        val songList = mutableListOf<SongEntity>()
        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        runCatching {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                "${MediaStore.Audio.Media.TITLE} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn) ?: "Unknown Track"
                    val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                    val album = cursor.getString(albumColumn) ?: "Unknown Album"
                    val albumId = cursor.getLong(albumIdColumn)
                    val duration = cursor.getLong(durationColumn)
                    val mimeType = cursor.getString(mimeTypeColumn) ?: "audio/mpeg"
                    val dateAdded = cursor.getLong(dateAddedColumn)

                    val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                    if (duration > 5000) { // filter out sound bites under 5s
                        songList.add(
                            SongEntity(
                                id = id,
                                title = title,
                                artist = if (artist == "<unknown>") "Local Artist" else artist,
                                album = if (album == "<unknown>") "Local Music" else album,
                                albumId = albumId,
                                durationMs = duration,
                                uriString = contentUri.toString(),
                                mimeType = mimeType,
                                isFavorite = false,
                                isDownloaded = true,
                                dateAdded = dateAdded * 1000L,
                                qualityTag = "Local Audio"
                            )
                        )
                    }
                }
            }
        }

        return songList
    }

    fun getSampleAuthorizedTracks(): List<SongEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            SongEntity(
                id = 1001L,
                title = "Lunar Echoes",
                artist = "Nova Collective",
                album = "Celestial Resonance",
                albumId = 101L,
                durationMs = 214_000L, // 3:34
                uriString = "asset:///sample_audio/lunar_echoes.mp3",
                mimeType = "audio/mp3",
                isFavorite = true,
                isDownloaded = true,
                dateAdded = now - 3600_000L * 24 * 2,
                playCount = 18,
                lastPlayedTimestamp = now - 1800_000L,
                qualityTag = "FLAC 24-bit • 96kHz",
                lyrics = """
                    [00:12.00] Drifting into the deep quiet space
                    [00:24.00] Echoes of lights we used to embrace
                    [00:38.00] A neon hum across the starlit sky
                    [00:52.00] Where memories and harmonies fly
                    [01:10.00] Lunar echoes guiding us home
                    [01:30.00] Through infinite soundscapes we roam
                """.trimIndent()
            ),
            SongEntity(
                id = 1002L,
                title = "Drift",
                artist = "Sola",
                album = "Atmospheric Horizons",
                albumId = 102L,
                durationMs = 185_000L, // 3:05
                uriString = "asset:///sample_audio/drift.mp3",
                mimeType = "audio/mp3",
                isFavorite = true,
                isDownloaded = true,
                dateAdded = now - 3600_000L * 24 * 5,
                playCount = 14,
                lastPlayedTimestamp = now - 7200_000L,
                qualityTag = "320kbps MP3",
                lyrics = """
                    [00:08.00] Soft breeze over amber lights
                    [00:20.00] Silent pulses through velvet nights
                    [00:35.00] Feel the rhythm slowly fade
                    [00:50.00] In the dreams that we have made
                """.trimIndent()
            ),
            SongEntity(
                id = 1003L,
                title = "Aperture Focus",
                artist = "The Prism Project",
                album = "Spectrum Dynamics",
                albumId = 103L,
                durationMs = 248_000L, // 4:08
                uriString = "asset:///sample_audio/aperture_focus.mp3",
                mimeType = "audio/mp3",
                isFavorite = false,
                isDownloaded = true,
                dateAdded = now - 3600_000L * 24 * 7,
                playCount = 9,
                lastPlayedTimestamp = now - 3600_000L * 20,
                qualityTag = "FLAC 16-bit",
                lyrics = """
                    [00:15.00] Light bends across the crystal glass
                    [00:30.00] Seconds turn to moments that pass
                    [00:48.00] Find the wavelength in the stream
                    [01:05.00] Alive inside a sonic dream
                """.trimIndent()
            ),
            SongEntity(
                id = 1004L,
                title = "Midnight Resonance",
                artist = "Aura Wave",
                album = "Nocturne Echoes",
                albumId = 104L,
                durationMs = 196_000L, // 3:16
                uriString = "asset:///sample_audio/midnight_resonance.mp3",
                mimeType = "audio/mp3",
                isFavorite = true,
                isDownloaded = true,
                dateAdded = now - 3600_000L * 24 * 10,
                playCount = 22,
                lastPlayedTimestamp = now - 3600_000L * 5,
                qualityTag = "320kbps MP3",
                lyrics = """
                    [00:10.00] Midnight shadows dance on the wall
                    [00:25.00] Hear the distant melody call
                    [00:40.00] Lost in the beat, safe in the sound
                    [00:58.00] Where peaceful frequencies are found
                """.trimIndent()
            ),
            SongEntity(
                id = 1005L,
                title = "Cyber Horizon",
                artist = "Synth Genesis",
                album = "Retro Futuristic",
                albumId = 105L,
                durationMs = 225_000L, // 3:45
                uriString = "asset:///sample_audio/cyber_horizon.mp3",
                mimeType = "audio/mp3",
                isFavorite = false,
                isDownloaded = true,
                dateAdded = now - 3600_000L * 24 * 12,
                playCount = 6,
                lastPlayedTimestamp = now - 3600_000L * 30,
                qualityTag = "320kbps MP3",
                lyrics = """
                    [00:14.00] Neon grid beneath the purple rain
                    [00:28.00] Traveling through a digital plane
                    [00:44.00] Faster than light, electric and free
                    [01:00.00] Horizons made of synthesizer ecstasy
                """.trimIndent()
            ),
            SongEntity(
                id = 1006L,
                title = "Ethereal Solitude",
                artist = "Sola",
                album = "Atmospheric Horizons",
                albumId = 102L,
                durationMs = 260_000L, // 4:20
                uriString = "asset:///sample_audio/ethereal_solitude.mp3",
                mimeType = "audio/mp3",
                isFavorite = true,
                isDownloaded = false,
                dateAdded = now - 3600_000L * 24 * 15,
                playCount = 11,
                lastPlayedTimestamp = now - 3600_000L * 15,
                qualityTag = "FLAC 24-bit",
                lyrics = """
                    [00:20.00] Stillness breathes in the morning dew
                    [00:40.00] Golden rays break clear and true
                    [01:00.00] Mind at ease in harmony
                    [01:25.00] Boundless sky as far as eye can see
                """.trimIndent()
            )
        )
    }
}
