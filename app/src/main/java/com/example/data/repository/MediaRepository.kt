package com.example.data.repository

import com.example.data.model.DeviceSyncInfo
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class MediaRepository private constructor() {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    private val _mediaItems = MutableStateFlow<List<MediaItem>>(initialMediaList())
    val mediaItems: StateFlow<List<MediaItem>> = _mediaItems.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _syncedDevices = MutableStateFlow(initialDevices())
    val syncedDevices: StateFlow<List<DeviceSyncInfo>> = _syncedDevices.asStateFlow()

    private val _isOfflineSimulated = MutableStateFlow(false)
    val isOfflineSimulated: StateFlow<Boolean> = _isOfflineSimulated.asStateFlow()

    private val _isVaultUnlocked = MutableStateFlow(true)
    val isVaultUnlocked: StateFlow<Boolean> = _isVaultUnlocked.asStateFlow()

    fun toggleOfflineSimulation() {
        _isOfflineSimulated.value = !_isOfflineSimulated.value
    }

    fun toggleVaultLock() {
        _isVaultUnlocked.value = !_isVaultUnlocked.value
    }

    fun unlockVault(pin: String): Boolean {
        if (pin == "1234" || pin.length == 4) {
            _isVaultUnlocked.value = true
            return true
        }
        return false
    }

    fun toggleFavorite(id: String) {
        _mediaItems.value = _mediaItems.value.map { item ->
            if (item.id == id) item.copy(isFavorite = !item.isFavorite) else item
        }
    }

    fun recordPlay(id: String) {
        _mediaItems.value = _mediaItems.value.map { item ->
            if (item.id == id) item.copy(playCount = item.playCount + 1) else item
        }
    }

    fun startDownload(id: String) {
        val current = _mediaItems.value.find { it.id == id } ?: return
        if (current.isDownloaded || current.isDownloading) return

        // Set downloading state
        _mediaItems.value = _mediaItems.value.map {
            if (it.id == id) it.copy(isDownloading = true, downloadProgress = 0.05f) else it
        }

        repositoryScope.launch {
            for (step in 1..10) {
                delay(220)
                _mediaItems.value = _mediaItems.value.map {
                    if (it.id == id) it.copy(downloadProgress = (step * 10) / 100f) else it
                }
            }
            _mediaItems.value = _mediaItems.value.map {
                if (it.id == id) it.copy(
                    isDownloading = false,
                    isDownloaded = true,
                    downloadProgress = 1.0f
                ) else it
            }
        }
    }

    fun removeDownload(id: String) {
        _mediaItems.value = _mediaItems.value.map {
            if (it.id == id) it.copy(isDownloaded = false, downloadProgress = 0f, isDownloading = false) else it
        }
    }

    fun clearAllCache() {
        _mediaItems.value = _mediaItems.value.map {
            it.copy(isDownloaded = false, downloadProgress = 0f, isDownloading = false)
        }
    }

    fun downloadAllPending() {
        val pending = _mediaItems.value.filter { !it.isDownloaded && !it.isDownloading }
        pending.forEach { startDownload(it.id) }
    }

    fun hostNewMedia(
        title: String,
        artist: String,
        type: MediaType,
        genre: String,
        isEncrypted: Boolean,
        quality: String = if (type == MediaType.AUDIO) "FLAC 24-bit 96kHz" else "1080p 60fps"
    ) {
        val newItem = MediaItem(
            id = UUID.randomUUID().toString(),
            title = title.ifBlank { "Personal Cloud Track" },
            artist = artist.ifBlank { "Klaus Robitics" },
            album = "Personal Vault Host",
            durationSeconds = if (type == MediaType.AUDIO) 214 else 340,
            streamUrl = if (type == MediaType.AUDIO)
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
            else
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            thumbnailUrl = if (type == MediaType.AUDIO)
                "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&q=80"
            else
                "https://images.unsplash.com/photo-1536240478700-b869070f9279?w=500&q=80",
            type = type,
            genre = genre.ifBlank { "Personal Vault" },
            fileSizeBytes = if (type == MediaType.AUDIO) 18_450_000L else 145_000_000L,
            quality = quality,
            isDownloaded = true,
            downloadProgress = 1.0f,
            isEncrypted = isEncrypted,
            cloudSynced = true,
            lyrics = if (type == MediaType.AUDIO) listOf(
                "Encrypted bitstream streaming from personal vault...",
                "Zero-latency decentralized audio cache active.",
                "Lossless master playback running smooth.",
                "End-to-end verified with SHA-384 hardware key."
            ) else emptyList()
        )
        _mediaItems.value = listOf(newItem) + _mediaItems.value
    }

    companion object {
        val instance: MediaRepository by lazy { MediaRepository() }

        private fun initialMediaList(): List<MediaItem> = listOf(
            MediaItem(
                id = "m1",
                title = "Neon Horizons (Synthwave Mix)",
                artist = "Aura Frequency",
                album = "Cybernetic Dreams",
                durationSeconds = 236,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                thumbnailUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&q=80",
                type = MediaType.AUDIO,
                genre = "Synthwave",
                fileSizeBytes = 24_600_000L,
                quality = "FLAC 24-bit 96kHz",
                isDownloaded = true,
                downloadProgress = 1.0f,
                isEncrypted = true,
                isFavorite = true,
                playCount = 42,
                cloudSynced = true,
                tags = listOf("Hi-Res", "Lossless", "Vault Encrypted", "Spatial Audio"),
                lyrics = listOf(
                    "[00:12] Midnight streetlights glowing neon cyan",
                    "[00:28] Cruising down the digital highway skyline",
                    "[00:45] Synthesizer frequencies pulsing in my veins",
                    "[01:05] Zero latency in our connected digital mind",
                    "[01:30] End-to-end encrypted signals in the dark",
                    "[02:00] Floating above the cybernetic city glow",
                    "[02:30] Lost inside the rhythm of eternity"
                )
            ),
            MediaItem(
                id = "m2",
                title = "Quantum Flow & Deep Bass",
                artist = "Sub-Zero Pulse",
                album = "Subatomic Oscillations",
                durationSeconds = 194,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                thumbnailUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&q=80",
                type = MediaType.AUDIO,
                genre = "Electronic",
                fileSizeBytes = 18_900_000L,
                quality = "FLAC 24-bit 48kHz",
                isDownloaded = true,
                downloadProgress = 1.0f,
                isEncrypted = false,
                isFavorite = true,
                playCount = 28,
                cloudSynced = true,
                tags = listOf("Bass Boost", "Club Master", "Hi-Fi"),
                lyrics = listOf(
                    "[00:10] Sub-bass waves vibrating the foundation",
                    "[00:30] Quantum particles locked in resonance",
                    "[00:55] Feel the kinetic low-end drop",
                    "[01:25] Sonic frequencies reaching absolute zero"
                )
            ),
            MediaItem(
                id = "v1",
                title = "Cyber City 2099: Neo Skyline",
                artist = "Vortex Cinema Lab",
                album = "Future Visions",
                durationSeconds = 345,
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&q=80",
                type = MediaType.VIDEO,
                genre = "Sci-Fi Visuals",
                fileSizeBytes = 245_000_000L,
                quality = "4K Ultra HD 60fps",
                isDownloaded = true,
                downloadProgress = 1.0f,
                isEncrypted = true,
                isFavorite = true,
                playCount = 37,
                cloudSynced = true,
                tags = listOf("4K UHD", "HDR10", "Offline Ready", "Atmos Audio")
            ),
            MediaItem(
                id = "m3",
                title = "Midnight Chillhop Study Beat",
                artist = "Lo-Fi Collective",
                album = "Rainy Café Sessions",
                durationSeconds = 178,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                thumbnailUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&q=80",
                type = MediaType.AUDIO,
                genre = "Chillhop",
                fileSizeBytes = 12_400_000L,
                quality = "320 kbps MP3",
                isDownloaded = false,
                downloadProgress = 0f,
                isEncrypted = false,
                isFavorite = false,
                playCount = 19,
                cloudSynced = true,
                tags = listOf("Lo-Fi", "Cozy", "Study", "Focus")
            ),
            MediaItem(
                id = "v2",
                title = "Deep Ocean Bio-Luminescence 4K",
                artist = "Earth Explorations",
                album = "Abyssal Secrets",
                durationSeconds = 412,
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=800&q=80",
                type = MediaType.VIDEO,
                genre = "Nature Documentary",
                fileSizeBytes = 380_000_000L,
                quality = "1080p 60fps",
                isDownloaded = false,
                downloadProgress = 0f,
                isEncrypted = false,
                isFavorite = true,
                playCount = 14,
                cloudSynced = true,
                tags = listOf("1080p", "Nature", "Cinematic", "Surround Sound")
            ),
            MediaItem(
                id = "m4",
                title = "Acoustic Sunset Sanctuary",
                artist = "Elena Rostova",
                album = "Wooden Strings & Amber",
                durationSeconds = 210,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
                thumbnailUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600&q=80",
                type = MediaType.AUDIO,
                genre = "Acoustic",
                fileSizeBytes = 16_800_000L,
                quality = "FLAC 24-bit 96kHz",
                isDownloaded = false,
                downloadProgress = 0f,
                isEncrypted = false,
                isFavorite = false,
                playCount = 9,
                cloudSynced = true,
                tags = listOf("Acoustic", "Warm", "Hi-Res")
            ),
            MediaItem(
                id = "v3",
                title = "Nebula Starflight: Interstellar Travel",
                artist = "Cosmos VFX Studio",
                album = "Space Voyager",
                durationSeconds = 295,
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800&q=80",
                type = MediaType.VIDEO,
                genre = "Sci-Fi Visuals",
                fileSizeBytes = 195_000_000L,
                quality = "1080p 60fps",
                isDownloaded = true,
                downloadProgress = 1.0f,
                isEncrypted = false,
                isFavorite = false,
                playCount = 22,
                cloudSynced = true,
                tags = listOf("Sci-Fi", "Space", "VFX")
            ),
            MediaItem(
                id = "m5",
                title = "Vault Master: Zero Knowledge Demo",
                artist = "Klaus Robitics (Encrypted Private)",
                album = "Secure Hardware Vault",
                durationSeconds = 152,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
                thumbnailUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600&q=80",
                type = MediaType.AUDIO,
                genre = "Private Audio",
                fileSizeBytes = 14_200_000L,
                quality = "Lossless 24-bit",
                isDownloaded = true,
                downloadProgress = 1.0f,
                isEncrypted = true,
                isFavorite = true,
                playCount = 55,
                cloudSynced = true,
                tags = listOf("E2EE Encrypted", "AES-256-GCM", "Hardware Vault"),
                lyrics = listOf(
                    "[00:05] Hardware enclave key authenticated.",
                    "[00:15] Decrypting on-the-fly inside secure sandbox.",
                    "[00:35] Zero telemetry shared with external servers.",
                    "[01:00] Complete cryptographic sovereignty."
                )
            ),
            MediaItem(
                id = "v4",
                title = "Tears of Steel: Cyber Drone Chase",
                artist = "Blender Open Movie",
                album = "Sci-Fi Action",
                durationSeconds = 312,
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&q=80",
                type = MediaType.VIDEO,
                genre = "Action Sci-Fi",
                fileSizeBytes = 280_000_000L,
                quality = "1080p 60fps",
                isDownloaded = false,
                downloadProgress = 0f,
                isEncrypted = true,
                isFavorite = false,
                playCount = 11,
                cloudSynced = true,
                tags = listOf("Action", "VFX", "Sci-Fi", "4K Ready")
            )
        )

        private fun initialDevices(): List<DeviceSyncInfo> = listOf(
            DeviceSyncInfo("d1", "Pixel 9 Pro (This Device)", "Android Phone", "Active Now", isCurrentDevice = true, ipAddress = "192.168.1.102"),
            DeviceSyncInfo("d2", "iPad Pro M4 13\"", "Tablet", "14 mins ago", isCurrentDevice = false, ipAddress = "192.168.1.109"),
            DeviceSyncInfo("d3", "MacBook Pro 16\" (M3 Max)", "MacBook", "1 hour ago", isCurrentDevice = false, ipAddress = "192.168.1.115"),
            DeviceSyncInfo("d4", "AuraStream Web Client (Chrome)", "Web Browser", "Yesterday", isCurrentDevice = false, ipAddress = "192.168.1.140")
        )
    }
}
