package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.repository.MediaRepository
import com.example.player.PlaybackManager
import com.example.ui.components.EncryptedVaultDialog
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.NowPlayingSheet
import com.example.ui.components.UploadMediaDialog
import com.example.ui.components.VideoPlayerDialog
import com.example.ui.components.VoiceSearchDialog
import com.example.ui.screens.CloudVaultScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MusicScreen
import com.example.ui.screens.VideoScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.AuraStreamTheme
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import kotlinx.coroutines.launch

enum class AppNavDestination(val title: String) {
    HOME("Discover"),
    MUSIC("Music"),
    VIDEOS("Videos"),
    DOWNLOADS("Offline"),
    VAULT("Cloud Vault")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraStreamApp() {
    val context = LocalContext.current
    val repository = remember { MediaRepository.instance }
    val playbackManager = remember { PlaybackManager(context) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Theme state
    var themeMode by remember { mutableStateOf(AppThemeMode.DARK_OBSIDIAN) }

    // Navigation state
    var currentNav by remember { mutableStateOf(AppNavDestination.HOME) }

    // Data streams
    val mediaItems by repository.mediaItems.collectAsState()
    val userProfile by repository.userProfile.collectAsState()
    val syncedDevices by repository.syncedDevices.collectAsState()
    val isOfflineSimulated by repository.isOfflineSimulated.collectAsState()
    val isVaultUnlocked by repository.isVaultUnlocked.collectAsState()

    // Player streams
    val currentAudio by playbackManager.currentMedia.collectAsState()
    val isPlayingAudio by playbackManager.isPlaying.collectAsState()
    val currentPosMs by playbackManager.currentPositionMs.collectAsState()
    val durationMs by playbackManager.durationMs.collectAsState()
    val isShuffle by playbackManager.isShuffle.collectAsState()
    val isRepeat by playbackManager.isRepeat.collectAsState()
    val playbackSpeed by playbackManager.playbackSpeed.collectAsState()
    val audioLatencyMode by playbackManager.audioLatencyMode.collectAsState()
    val equalizerPreset by playbackManager.equalizerPreset.collectAsState()
    val visualizerAmplitudes by playbackManager.visualizerAmplitudes.collectAsState()

    // Dialog & Sheet states
    var showNowPlayingSheet by remember { mutableStateOf(false) }
    var activeVideoItem by remember { mutableStateOf<MediaItem?>(null) }
    var showVoiceSearchDialog by remember { mutableStateOf(false) }
    var showVaultAuthDialog by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }

    // Handle Back Press
    BackHandler(enabled = showNowPlayingSheet || activeVideoItem != null || showVoiceSearchDialog || showVaultAuthDialog || showUploadDialog || currentNav != AppNavDestination.HOME) {
        when {
            showNowPlayingSheet -> showNowPlayingSheet = false
            activeVideoItem != null -> activeVideoItem = null
            showVoiceSearchDialog -> showVoiceSearchDialog = false
            showVaultAuthDialog -> showVaultAuthDialog = false
            showUploadDialog -> showUploadDialog = false
            currentNav != AppNavDestination.HOME -> currentNav = AppNavDestination.HOME
        }
    }

    AuraStreamTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    // Header Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { currentNav = AppNavDestination.HOME }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Brush.linearGradient(listOf(NeonCyan, ElectricViolet))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "AURA",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "STREAM",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        ),
                                        color = NeonCyan
                                    )
                                }
                                Text(
                                    text = if (isOfflineSimulated) "OFFLINE VAULT" else "CLOUD VAULT 5G",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = if (isOfflineSimulated) Color(0xFFFFB703) else NeonEmerald
                                )
                            }
                        }

                        // Top Action Icons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Quick Theme Mode Toggle
                            IconButton(
                                onClick = {
                                    themeMode = when (themeMode) {
                                        AppThemeMode.DARK_OBSIDIAN -> AppThemeMode.AMOLED_BLACK
                                        AppThemeMode.AMOLED_BLACK -> AppThemeMode.LIGHT
                                        AppThemeMode.LIGHT -> AppThemeMode.DARK_OBSIDIAN
                                        AppThemeMode.SYSTEM -> AppThemeMode.DARK_OBSIDIAN
                                    }
                                    coroutineScope.launch {
                                        val name = when (themeMode) {
                                            AppThemeMode.AMOLED_BLACK -> "AMOLED Black (OLED saver)"
                                            AppThemeMode.LIGHT -> "Light Theme"
                                            else -> "Obsidian Dark"
                                        }
                                        snackbarHostState.showSnackbar("Switched to $name")
                                    }
                                },
                                modifier = Modifier.testTag("theme_toggle_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Brightness4,
                                    contentDescription = "Toggle Dark/AMOLED/Light",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // User Profile Avatar
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, NeonCyan, CircleShape)
                                    .clickable { currentNav = AppNavDestination.VAULT }
                                    .testTag("top_user_avatar"),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = "file:///android_asset/user_avatar.jpg",
                                    contentDescription = "Klaus Robitics",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                },
                bottomBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        // Floating Mini Player docked above Bottom Nav
                        val progressFraction = if (durationMs > 0) currentPosMs.toFloat() / durationMs.toFloat() else 0f
                        MiniPlayerBar(
                            currentMedia = currentAudio,
                            isPlaying = isPlayingAudio,
                            progressFraction = progressFraction,
                            visualizerAmplitudes = visualizerAmplitudes,
                            onTogglePlayPause = { playbackManager.togglePlayPause() },
                            onSkipNext = { playbackManager.playNext() },
                            onClickBar = { showNowPlayingSheet = true }
                        )

                        // Bottom Navigation Bar
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp,
                            modifier = Modifier.fillMaxWidth().testTag("main_bottom_nav")
                        ) {
                            val items = listOf(
                                Triple(AppNavDestination.HOME, Icons.Filled.Explore, Icons.Outlined.Explore),
                                Triple(AppNavDestination.MUSIC, Icons.Filled.MusicNote, Icons.Outlined.MusicNote),
                                Triple(AppNavDestination.VIDEOS, Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary),
                                Triple(AppNavDestination.DOWNLOADS, Icons.Filled.DownloadDone, Icons.Outlined.DownloadDone),
                                Triple(AppNavDestination.VAULT, Icons.Filled.Cloud, Icons.Outlined.Cloud)
                            )

                            items.forEach { (destination, selectedIcon, unselectedIcon) ->
                                val isSelected = currentNav == destination
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentNav = destination },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                            contentDescription = destination.title,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = destination.title,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 10.sp
                                            )
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = NeonCyan,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("nav_${destination.name.lowercase()}")
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Crossfade(targetState = currentNav, label = "screen_crossfade") { screen ->
                        when (screen) {
                            AppNavDestination.HOME -> HomeScreen(
                                mediaItems = mediaItems,
                                isOfflineSimulated = isOfflineSimulated,
                                onToggleOfflineSimulation = { repository.toggleOfflineSimulation() },
                                onPlayAudio = { track ->
                                    playbackManager.play(track)
                                    showNowPlayingSheet = true
                                },
                                onPlayVideo = { video -> activeVideoItem = video },
                                onStartDownload = { id ->
                                    repository.startDownload(id)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Downloading into offline encrypted cache...")
                                    }
                                },
                                onToggleFavorite = { repository.toggleFavorite(it) },
                                onOpenVoiceSearch = { showVoiceSearchDialog = true },
                                onOpenHostMedia = { showUploadDialog = true }
                            )

                            AppNavDestination.MUSIC -> MusicScreen(
                                musicItems = mediaItems,
                                currentlyPlayingId = currentAudio?.id,
                                isPlaying = isPlayingAudio,
                                onPlayAudio = { track ->
                                    playbackManager.play(track)
                                    showNowPlayingSheet = true
                                },
                                onStartDownload = { id ->
                                    repository.startDownload(id)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Downloading audio stream offline...")
                                    }
                                },
                                onToggleFavorite = { repository.toggleFavorite(it) },
                                onOpenHostMedia = { showUploadDialog = true }
                            )

                            AppNavDestination.VIDEOS -> VideoScreen(
                                videoItems = mediaItems,
                                onPlayVideo = { video -> activeVideoItem = video },
                                onStartDownload = { id ->
                                    repository.startDownload(id)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Downloading 4K/HD video offline...")
                                    }
                                },
                                onToggleFavorite = { repository.toggleFavorite(it) },
                                onOpenHostVideo = { showUploadDialog = true }
                            )

                            AppNavDestination.DOWNLOADS -> DownloadsScreen(
                                downloadedItems = mediaItems.filter { it.isDownloaded },
                                onPlayAudio = { track ->
                                    playbackManager.play(track)
                                    showNowPlayingSheet = true
                                },
                                onPlayVideo = { video -> activeVideoItem = video },
                                onRemoveDownload = { id ->
                                    repository.removeDownload(id)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Removed from offline storage")
                                    }
                                },
                                onClearAllCache = {
                                    repository.clearAllCache()
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Cleared offline media cache")
                                    }
                                },
                                onDownloadAllPending = {
                                    repository.downloadAllPending()
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Queueing all media for offline download...")
                                    }
                                }
                            )

                            AppNavDestination.VAULT -> CloudVaultScreen(
                                userProfile = userProfile,
                                syncedDevices = syncedDevices,
                                isVaultUnlocked = isVaultUnlocked,
                                currentThemeMode = themeMode,
                                onSelectTheme = { themeMode = it },
                                onOpenVaultAuth = { showVaultAuthDialog = true }
                            )
                        }
                    }
                }
            }

            // MODALS & DIALOGS
            // 1. Now Playing Bottom Sheet
            if (showNowPlayingSheet) {
                NowPlayingSheet(
                    mediaItem = currentAudio,
                    isPlaying = isPlayingAudio,
                    currentPositionMs = currentPosMs,
                    durationMs = durationMs,
                    isShuffle = isShuffle,
                    isRepeat = isRepeat,
                    playbackSpeed = playbackSpeed,
                    audioLatencyMode = audioLatencyMode,
                    equalizerPreset = equalizerPreset,
                    visualizerAmplitudes = visualizerAmplitudes,
                    onTogglePlayPause = { playbackManager.togglePlayPause() },
                    onSeekTo = { playbackManager.seekTo(it) },
                    onSkipNext = { playbackManager.playNext() },
                    onSkipPrevious = { playbackManager.playPrevious() },
                    onToggleShuffle = { playbackManager.toggleShuffle() },
                    onToggleRepeat = { playbackManager.toggleRepeat() },
                    onSetSpeed = { playbackManager.setSpeed(it) },
                    onSetLatencyMode = { playbackManager.setLatencyMode(it) },
                    onSetEqualizer = { playbackManager.setEqualizerPreset(it) },
                    onToggleFavorite = { repository.toggleFavorite(it) },
                    onDismiss = { showNowPlayingSheet = false }
                )
            }

            // 2. Video Player Modal Dialog
            if (activeVideoItem != null) {
                VideoPlayerDialog(
                    videoItem = activeVideoItem,
                    onDismiss = { activeVideoItem = null },
                    onStartDownload = { id ->
                        repository.startDownload(id)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Downloading video for offline viewing...")
                        }
                    },
                    onToggleFavorite = { repository.toggleFavorite(it) }
                )
            }

            // 3. Voice Command Dialog
            if (showVoiceSearchDialog) {
                VoiceSearchDialog(
                    onDismiss = { showVoiceSearchDialog = false },
                    onVoiceResult = { query ->
                        coroutineScope.launch {
                            val matched = mediaItems.firstOrNull {
                                it.title.contains(query, ignoreCase = true) ||
                                        query.contains(it.title, ignoreCase = true) ||
                                        query.contains(it.genre, ignoreCase = true)
                            }
                            if (matched != null) {
                                if (matched.type == MediaType.AUDIO) {
                                    playbackManager.play(matched)
                                    showNowPlayingSheet = true
                                    snackbarHostState.showSnackbar("Voice match: Playing ${matched.title}")
                                } else {
                                    activeVideoItem = matched
                                    snackbarHostState.showSnackbar("Voice match: Opening ${matched.title}")
                                }
                            } else {
                                snackbarHostState.showSnackbar("Command received: \"$query\"")
                            }
                        }
                    }
                )
            }

            // 4. Encrypted Vault PIN / Biometric Auth Dialog
            if (showVaultAuthDialog) {
                EncryptedVaultDialog(
                    onDismiss = { showVaultAuthDialog = false },
                    onUnlocked = {
                        repository.toggleVaultLock()
                        coroutineScope.launch {
                            val status = if (repository.isVaultUnlocked.value) "Unlocked" else "Locked"
                            snackbarHostState.showSnackbar("AES-256 Vault is now $status")
                        }
                    }
                )
            }

            // 5. Host & Upload Personal Media Dialog
            if (showUploadDialog) {
                UploadMediaDialog(
                    onDismiss = { showUploadDialog = false },
                    onConfirmUpload = { title, artist, type, genre, isEncrypted ->
                        repository.hostNewMedia(
                            title = title,
                            artist = artist,
                            type = type,
                            genre = genre,
                            isEncrypted = isEncrypted
                        )
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Hosted \"$title\" in your cloud vault!")
                        }
                    }
                )
            }
        }
    }
}
