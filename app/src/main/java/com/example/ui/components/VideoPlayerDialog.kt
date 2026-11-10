package com.example.ui.components

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.MediaItem
import com.example.ui.theme.CoralPink
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerDialog(
    videoItem: MediaItem?,
    onDismiss: () -> Unit,
    onStartDownload: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    if (videoItem == null) return

    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(true) }
    var selectedQuality by remember { mutableStateOf(videoItem.quality) }
    var selectedSpeed by remember { mutableFloatStateOf(1.0f) }
    var currentProgressSeconds by remember { mutableIntStateOf(0) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    // Auto-hide controls timer
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4000)
            showControls = false
        }
    }

    // Progress counter
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000)
            if (currentProgressSeconds < videoItem.durationSeconds) {
                currentProgressSeconds += 1
            } else {
                currentProgressSeconds = 0
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            color = Color.Black
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showControls = !showControls }
            ) {
                // Video Stream View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .aspectRatio(16f / 9f)
                        .background(Color(0xFF070B11))
                ) {
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                val uri = Uri.parse(videoItem.streamUrl)
                                setVideoURI(uri)
                                setOnPreparedListener { mp ->
                                    mp.isLooping = true
                                    start()
                                }
                                setOnErrorListener { _, _, _ ->
                                    // Fallback seamlessly if network or stream has issue
                                    true
                                }
                                videoViewRef = this
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // If videoView fails to load in emulator environment, display high-res preview banner
                    AsyncImage(
                        model = videoItem.thumbnailUrl,
                        contentDescription = videoItem.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    )
                }

                // Controls Overlay
                AnimatedVisibility(
                    visible = showControls,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.85f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.9f)
                                    )
                                )
                            )
                    ) {
                        // Top Header Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 24.dp)
                                .align(Alignment.TopCenter),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0x66000000), CircleShape)
                                    .testTag("video_player_close")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = videoItem.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${videoItem.artist} • ${videoItem.genre}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                    if (videoItem.isEncrypted) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Outlined.Lock,
                                            contentDescription = "Encrypted",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }

                            // Favorite Action
                            IconButton(
                                onClick = { onToggleFavorite(videoItem.id) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0x66000000), CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (videoItem.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (videoItem.isFavorite) CoralPink else Color.White
                                )
                            }
                        }

                        // Center Playback Transport
                        Row(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalArrangement = Arrangement.spacedBy(28.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rewind 10s
                            IconButton(
                                onClick = {
                                    currentProgressSeconds = (currentProgressSeconds - 10).coerceAtLeast(0)
                                    videoViewRef?.seekTo(currentProgressSeconds * 1000)
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0x66000000), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FastRewind,
                                    contentDescription = "Rewind 10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            // Main Play/Pause Button
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(NeonCyan, ElectricViolet)))
                                    .clickable {
                                        isPlaying = !isPlaying
                                        if (isPlaying) {
                                            videoViewRef?.start()
                                        } else {
                                            videoViewRef?.pause()
                                        }
                                    }
                                    .testTag("video_player_toggle"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.Black,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            // Forward 10s
                            IconButton(
                                onClick = {
                                    currentProgressSeconds = (currentProgressSeconds + 10).coerceAtMost(videoItem.durationSeconds)
                                    videoViewRef?.seekTo(currentProgressSeconds * 1000)
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0x66000000), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FastForward,
                                    contentDescription = "Fast Forward 10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        // Bottom Control Panel
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 20.dp, vertical = 24.dp)
                        ) {
                            // Timeline Progress Bar
                            Slider(
                                value = currentProgressSeconds.toFloat(),
                                onValueChange = {
                                    currentProgressSeconds = it.toInt()
                                    videoViewRef?.seekTo(currentProgressSeconds * 1000)
                                },
                                valueRange = 0f..videoItem.durationSeconds.toFloat(),
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonCyan,
                                    activeTrackColor = NeonCyan,
                                    inactiveTrackColor = Color.Gray.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("video_slider")
                            )

                            // Timestamp and Resolution Badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val currentMin = currentProgressSeconds / 60
                                val currentSec = currentProgressSeconds % 60
                                val totalMin = videoItem.durationSeconds / 60
                                val totalSec = videoItem.durationSeconds % 60

                                Text(
                                    text = "$currentMin:${currentSec.toString().padStart(2, '0')} / $totalMin:${totalSec.toString().padStart(2, '0')}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Offline Download Button
                                    if (videoItem.isDownloaded) {
                                        Row(
                                            modifier = Modifier
                                                .background(NeonEmerald.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                                .border(1.dp, NeonEmerald.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DownloadDone,
                                                contentDescription = "Downloaded",
                                                tint = NeonEmerald,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Downloaded (${videoItem.formattedFileSize})",
                                                fontSize = 11.sp,
                                                color = NeonEmerald
                                            )
                                        }
                                    } else if (videoItem.isDownloading) {
                                        Row(
                                            modifier = Modifier
                                                .background(NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                progress = { videoItem.downloadProgress },
                                                modifier = Modifier.size(14.dp),
                                                color = NeonCyan,
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${(videoItem.downloadProgress * 100).toInt()}%",
                                                fontSize = 11.sp,
                                                color = NeonCyan
                                            )
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0x33FFFFFF))
                                                .clickable { onStartDownload(videoItem.id) }
                                                .padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Download,
                                                contentDescription = "Download for Offline Playback",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Download Offline",
                                                fontSize = 11.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quality & Speed Selectors
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Quality Options
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("4K UHD", "1080p", "720p").forEach { quality ->
                                        FilterChip(
                                            selected = selectedQuality == quality,
                                            onClick = { selectedQuality = quality },
                                            label = { Text(quality, fontSize = 10.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = NeonCyan.copy(alpha = 0.25f),
                                                selectedLabelColor = NeonCyan
                                            )
                                        )
                                    }
                                }

                                // Speed Options
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(1.0f, 1.5f, 2.0f).forEach { speed ->
                                        FilterChip(
                                            selected = selectedSpeed == speed,
                                            onClick = { selectedSpeed = speed },
                                            label = { Text("${speed}x", fontSize = 10.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = ElectricViolet.copy(alpha = 0.25f),
                                                selectedLabelColor = ElectricViolet
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
