package com.example.data.remote

import com.example.data.model.*
import kotlinx.coroutines.delay

object AuthorizedMusicCatalog {

    // Royalty-free / CC / Public Domain verified streaming URLs and tracks
    private val allCatalogTracks = listOf(
        OnlineTrack(
            id = "novatune_on_01",
            title = "Midnight Horizon",
            artist = "Aether Wave",
            album = "Neon Twilight",
            durationMs = 218_000L, // 3:38
            coverUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            downloadUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            isDownloadPermitted = true,
            licenseType = "Creative Commons BY 4.0",
            licenseUrl = "https://creativecommons.org/licenses/by/4.0/",
            audioQualityTag = "320kbps MP3",
            expectedSizeBytes = 8_720_000L,
            genre = "Synthwave",
            releaseYear = 2024,
            isStreamOnly = false,
            playCount = 38400,
            lyrics = "[00:12.00]Neon lights in the twilight glow\n[00:28.00]Shadows dance on the road below\n[00:45.00]Racing into the midnight sky\n[01:02.00]Lost where the retro highways lie\n[01:20.00]Synthesizers hum in deep rhythm\n[01:48.00]Floating past the digital prism\n[02:10.00]Midnight horizon calling you home"
        ),
        OnlineTrack(
            id = "novatune_on_02",
            title = "Cosmic Reverie",
            artist = "Starlight Ensemble",
            album = "Orbit of Tranquility",
            durationMs = 274_000L, // 4:34
            coverUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            downloadUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            isDownloadPermitted = true,
            licenseType = "CC0 1.0 Public Domain",
            licenseUrl = "https://creativecommons.org/publicdomain/zero/1.0/",
            audioQualityTag = "FLAC Lossless",
            expectedSizeBytes = 28_400_000L,
            genre = "Ambient",
            releaseYear = 2024,
            isStreamOnly = false,
            playCount = 52100,
            lyrics = "[00:15.00]Soft planetary pulses\n[00:40.00]Drifting through interstellar quiet\n[01:10.00]Gravity lets go of time\n[01:45.00]Echoes of starlight in your mind"
        ),
        OnlineTrack(
            id = "novatune_on_03",
            title = "Velvet Rain",
            artist = "Komorebi Duo",
            album = "Coffee & Raindrops",
            durationMs = 186_000L, // 3:06
            coverUrl = "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            downloadUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            isDownloadPermitted = true,
            licenseType = "Novatune Certified CC-BY",
            licenseUrl = "https://creativecommons.org/licenses/by/4.0/",
            audioQualityTag = "320kbps MP3",
            expectedSizeBytes = 7_440_000L,
            genre = "Lo-Fi",
            releaseYear = 2023,
            isStreamOnly = false,
            playCount = 89300,
            lyrics = "[00:08.00]Drops tapping on the windowpane\n[00:24.00]Warm cup steaming in the morning rain\n[00:42.00]Lo-fi guitar chords strummed slow\n[01:00.00]Letting all the heavy worries go"
        ),
        OnlineTrack(
            id = "novatune_on_04",
            title = "Solar Wind Odyssey",
            artist = "Quantum Echoes",
            album = "Hyperdrive Chronicles",
            durationMs = 245_000L, // 4:05
            coverUrl = "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            downloadUrl = null, // Stream-only licensed track!
            isDownloadPermitted = false,
            licenseType = "Novatune Stream-Only License",
            licenseUrl = "https://novatune.audio/licenses/streaming-only",
            audioQualityTag = "320kbps Stream",
            expectedSizeBytes = 9_800_000L,
            genre = "Electronic",
            releaseYear = 2024,
            isStreamOnly = true,
            playCount = 64100,
            lyrics = "[00:10.00]Engaging solar sails\n[00:30.00]Accelerating beyond the orbit\n[01:00.00]Pulsing bassline surges\n[01:30.00]Across the interstellar rift"
        ),
        OnlineTrack(
            id = "novatune_on_05",
            title = "Autumnal Waltz",
            artist = "Elena Rostova & Chamber Quartet",
            album = "Seasons in Harmony",
            durationMs = 202_000L, // 3:22
            coverUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            downloadUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            isDownloadPermitted = true,
            licenseType = "Public Domain Dedication",
            licenseUrl = "https://creativecommons.org/publicdomain/zero/1.0/",
            audioQualityTag = "FLAC Lossless",
            expectedSizeBytes = 22_500_000L,
            genre = "Orchestral",
            releaseYear = 2023,
            isStreamOnly = false,
            playCount = 27600,
            lyrics = "Instrumental performance featuring violin, cello, viola, and grand piano."
        ),
        OnlineTrack(
            id = "novatune_on_06",
            title = "Subway Sunset Groove",
            artist = "Urban Chill Collective",
            album = "Metro Echoes",
            durationMs = 195_000L, // 3:15
            coverUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            downloadUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            isDownloadPermitted = true,
            licenseType = "Creative Commons BY-SA 4.0",
            licenseUrl = "https://creativecommons.org/licenses/by-sa/4.0/",
            audioQualityTag = "320kbps MP3",
            expectedSizeBytes = 7_800_000L,
            genre = "Chillhop",
            releaseYear = 2024,
            isStreamOnly = false,
            playCount = 41200,
            lyrics = "[00:14.00]Riding the 7 train at sundown\n[00:32.00]Golden rays upon the steel rails\n[00:50.00]Headphones tight with dusty beats\n[01:10.00]Watching the city breathe below"
        ),
        OnlineTrack(
            id = "novatune_on_07",
            title = "Aurora Borealis Dream",
            artist = "Nordic Soundscapes",
            album = "Fjord Whispers",
            durationMs = 310_000L, // 5:10
            coverUrl = "https://images.unsplash.com/photo-1531366936337-7c912a4589a7?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            downloadUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            isDownloadPermitted = true,
            licenseType = "Creative Commons BY 4.0",
            licenseUrl = "https://creativecommons.org/licenses/by/4.0/",
            audioQualityTag = "320kbps MP3",
            expectedSizeBytes = 12_400_000L,
            genre = "Ambient",
            releaseYear = 2024,
            isStreamOnly = false,
            playCount = 76500,
            lyrics = "[00:30.00]Green lights dance across the Arctic frost\n[01:15.00]Silent mountains where footsteps are lost\n[02:00.00]Breathing the pure crystalline air"
        ),
        OnlineTrack(
            id = "novatune_on_08",
            title = "Digital Horizon 2088",
            artist = "Cyber Knight",
            album = "Neo Tokyo Drive",
            durationMs = 230_000L, // 3:50
            coverUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://raw.githubusercontent.com/rafaelreis-hotmart/Audio-Sample-files/master/sample.mp3",
            downloadUrl = null, // Stream only
            isDownloadPermitted = false,
            licenseType = "Novatune Stream-Only License",
            licenseUrl = "https://novatune.audio/licenses/streaming-only",
            audioQualityTag = "320kbps Stream",
            expectedSizeBytes = 9_200_000L,
            genre = "Synthwave",
            releaseYear = 2024,
            isStreamOnly = true,
            playCount = 98400,
            lyrics = "[00:20.00]Neon billboards reflect in the visor\n[00:40.00]Speeding along the high expressway\n[01:05.00]Synthetic adrenaline rising high"
        )
    )

    private val allArtists = listOf(
        OnlineArtist(
            id = "art_01",
            name = "Aether Wave",
            genre = "Synthwave",
            isVerified = true,
            avatarUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=300&auto=format&fit=crop&q=80",
            monthlyListeners = "184,200",
            bio = "Pioneering retro-futuristic soundscapes and vibrant analog synth melodies since 2019."
        ),
        OnlineArtist(
            id = "art_02",
            name = "Starlight Ensemble",
            genre = "Ambient",
            isVerified = true,
            avatarUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=300&auto=format&fit=crop&q=80",
            monthlyListeners = "310,500",
            bio = "International collective exploring acoustic resonance and cosmic field recordings."
        ),
        OnlineArtist(
            id = "art_03",
            name = "Komorebi Duo",
            genre = "Lo-Fi",
            isVerified = true,
            avatarUrl = "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?w=300&auto=format&fit=crop&q=80",
            monthlyListeners = "495,000",
            bio = "Warm tape-saturated beats, vinyl crackles, and gentle electric piano grooves for relaxation."
        ),
        OnlineArtist(
            id = "art_04",
            name = "Elena Rostova & Chamber Quartet",
            genre = "Orchestral",
            isVerified = true,
            avatarUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=300&auto=format&fit=crop&q=80",
            monthlyListeners = "128,900",
            bio = "Classical virtuosic chamber arrangements celebrated for emotional depth and acoustic clarity."
        ),
        OnlineArtist(
            id = "art_05",
            name = "Urban Chill Collective",
            genre = "Chillhop",
            isVerified = true,
            avatarUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=300&auto=format&fit=crop&q=80",
            monthlyListeners = "260,300",
            bio = "Melodic urban beats crafted for late night studying, commuting, and calm creativity."
        )
    )

    private val allAlbums = listOf(
        OnlineAlbum(
            id = "alb_01",
            title = "Neon Twilight",
            artist = "Aether Wave",
            releaseYear = 2024,
            coverUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&auto=format&fit=crop&q=80",
            trackCount = 10,
            genre = "Synthwave"
        ),
        OnlineAlbum(
            id = "alb_02",
            title = "Orbit of Tranquility",
            artist = "Starlight Ensemble",
            releaseYear = 2024,
            coverUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=400&auto=format&fit=crop&q=80",
            trackCount = 8,
            genre = "Ambient"
        ),
        OnlineAlbum(
            id = "alb_03",
            title = "Coffee & Raindrops",
            artist = "Komorebi Duo",
            releaseYear = 2023,
            coverUrl = "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?w=400&auto=format&fit=crop&q=80",
            trackCount = 12,
            genre = "Lo-Fi"
        ),
        OnlineAlbum(
            id = "alb_04",
            title = "Seasons in Harmony",
            artist = "Elena Rostova & Chamber Quartet",
            releaseYear = 2023,
            coverUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=400&auto=format&fit=crop&q=80",
            trackCount = 9,
            genre = "Orchestral"
        )
    )

    private val allPlaylists = listOf(
        OnlinePlaylist(
            id = "curated_01",
            title = "Midnight Cyber Drive",
            description = "High octane synthwave, darksynth, and retro pulse tracks for nighttime journeys",
            coverGradientStart = 0xFF4A2B5EL,
            coverGradientEnd = 0xFF1A3C5EL,
            trackCount = 24,
            curator = "Novatune Curators",
            tracks = allCatalogTracks.filter { it.genre == "Synthwave" || it.genre == "Electronic" }
        ),
        OnlinePlaylist(
            id = "curated_02",
            title = "Deep Space Focus & Sleep",
            description = "Zero-gravity ambient soundscapes engineered for deep work and undisturbed rest",
            coverGradientStart = 0xFF064E3BL,
            coverGradientEnd = 0xFF0F172AL,
            trackCount = 18,
            curator = "Novatune Ambient Lab",
            tracks = allCatalogTracks.filter { it.genre == "Ambient" }
        ),
        OnlinePlaylist(
            id = "curated_03",
            title = "Lo-Fi Study Café",
            description = "Mellow beats, cozy acoustic loops, and warm vinyl grooves for reading and study",
            coverGradientStart = 0xFF9A3412L,
            coverGradientEnd = 0xFF4C0519L,
            trackCount = 30,
            curator = "Chillhop Weekly",
            tracks = allCatalogTracks.filter { it.genre == "Lo-Fi" || it.genre == "Chillhop" }
        ),
        OnlinePlaylist(
            id = "curated_04",
            title = "Acoustic Classical Masterpieces",
            description = "Authorized classical performances by chamber quartets and solo pianists",
            coverGradientStart = 0xFF312E81L,
            coverGradientEnd = 0xFF1E1B4BL,
            trackCount = 15,
            curator = "Classical Heritage",
            tracks = allCatalogTracks.filter { it.genre == "Orchestral" }
        )
    )

    val popularGenres = listOf(
        "All Genres", "Synthwave", "Ambient", "Lo-Fi", "Electronic", "Orchestral", "Chillhop", "Jazz & Blues", "Cyberpunk", "Acoustic"
    )

    val searchSuggestions = listOf(
        "Midnight Horizon", "Lo-Fi Study", "Starlight Ensemble", "Synthwave 80s", "Cyber Aurora", "Velvet Rain", "Aether Wave", "Ambient Focus"
    )

    suspend fun searchCatalog(
        query: String,
        filter: SearchFilterCategory = SearchFilterCategory.ALL
    ): OnlineSearchResult {
        delay(180L) // Subtle realistic network response latency
        val q = query.trim().lowercase()

        if (q.isEmpty()) {
            return OnlineSearchResult(
                query = "",
                songs = allCatalogTracks,
                artists = allArtists,
                albums = allAlbums,
                playlists = allPlaylists,
                genres = popularGenres,
                totalCount = allCatalogTracks.size
            )
        }

        val matchedSongs = allCatalogTracks.filter {
            it.title.lowercase().contains(q) ||
            it.artist.lowercase().contains(q) ||
            it.album.lowercase().contains(q) ||
            it.genre.lowercase().contains(q)
        }

        val matchedArtists = allArtists.filter {
            it.name.lowercase().contains(q) || it.genre.lowercase().contains(q)
        }

        val matchedAlbums = allAlbums.filter {
            it.title.lowercase().contains(q) || it.artist.lowercase().contains(q) || it.genre.lowercase().contains(q)
        }

        val matchedPlaylists = allPlaylists.filter {
            it.title.lowercase().contains(q) || it.description.lowercase().contains(q)
        }

        val matchedGenres = popularGenres.filter {
            it.lowercase().contains(q)
        }

        val total = when (filter) {
            SearchFilterCategory.ALL -> matchedSongs.size + matchedArtists.size + matchedAlbums.size + matchedPlaylists.size
            SearchFilterCategory.SONGS -> matchedSongs.size
            SearchFilterCategory.ARTISTS -> matchedArtists.size
            SearchFilterCategory.ALBUMS -> matchedAlbums.size
            SearchFilterCategory.PLAYLISTS -> matchedPlaylists.size
            SearchFilterCategory.GENRES -> matchedGenres.size
        }

        return OnlineSearchResult(
            query = query,
            songs = if (filter == SearchFilterCategory.ALL || filter == SearchFilterCategory.SONGS) matchedSongs else emptyList(),
            artists = if (filter == SearchFilterCategory.ALL || filter == SearchFilterCategory.ARTISTS) matchedArtists else emptyList(),
            albums = if (filter == SearchFilterCategory.ALL || filter == SearchFilterCategory.ALBUMS) matchedAlbums else emptyList(),
            playlists = if (filter == SearchFilterCategory.ALL || filter == SearchFilterCategory.PLAYLISTS) matchedPlaylists else emptyList(),
            genres = if (filter == SearchFilterCategory.ALL || filter == SearchFilterCategory.GENRES) matchedGenres else emptyList(),
            totalCount = total
        )
    }

    fun getFeaturedTracks(): List<OnlineTrack> = allCatalogTracks.take(5)

    fun getRecommendedTracks(): List<OnlineTrack> = allCatalogTracks.shuffled().take(6)

    fun getCuratedPlaylists(): List<OnlinePlaylist> = allPlaylists

    fun getTrackById(trackId: String): OnlineTrack? = allCatalogTracks.find { it.id == trackId }
}
