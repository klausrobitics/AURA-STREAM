package com.example.data.model

enum class MediaType {
    AUDIO,
    VIDEO
}

data class MediaItem(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val durationSeconds: Int,
    val streamUrl: String,
    val thumbnailUrl: String,
    val type: MediaType,
    val genre: String,
    val fileSizeBytes: Long,
    val quality: String, // e.g., "FLAC 24-bit 96kHz", "1080p 60fps", "4K Ultra HD"
    val isDownloaded: Boolean = false,
    val downloadProgress: Float = 0f, // 0f to 1f
    val isDownloading: Boolean = false,
    val isEncrypted: Boolean = false,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lyrics: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val cloudSynced: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    val formattedDuration: String
        get() {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return "$minutes:${seconds.toString().padStart(2, '0')}"
        }

    val formattedFileSize: String
        get() {
            val mb = fileSizeBytes / (1024.0 * 1024.0)
            return if (mb >= 1000) {
                String.format("%.1f GB", mb / 1024.0)
            } else {
                String.format("%.1f MB", mb)
            }
        }
}

data class UserProfile(
    val name: String = "Klaus Robitics",
    val email: String = "klausrobitics@gmail.com",
    val avatarPath: String = "user_avatar.jpg",
    val tier: String = "Pro Vault Ultimate",
    val totalStorageBytes: Long = 53_687_091_200L, // 50 GB
    val usedStorageBytes: Long = 13_315_522_560L,  // ~12.4 GB
    val audioStorageBytes: Long = 5_153_960_755L,  // ~4.8 GB
    val videoStorageBytes: Long = 7_623_565_312L,  // ~7.1 GB
    val encryptedVaultBytes: Long = 537_996_493L,  // ~0.5 GB
    val encryptionKeyFingerprint: String = "AES-256-GCM • SHA-384-F8B2...9E41",
    val isVaultLocked: Boolean = false
)

data class DeviceSyncInfo(
    val id: String,
    val deviceName: String,
    val deviceType: String, // "Android Phone", "Tablet", "MacBook", "Web Browser"
    val lastSyncTime: String,
    val isCurrentDevice: Boolean = false,
    val ipAddress: String = "192.168.1.14"
)
