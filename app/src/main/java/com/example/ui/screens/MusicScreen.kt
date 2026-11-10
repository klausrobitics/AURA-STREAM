package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald

@Composable
fun MusicScreen(
    musicItems: List<MediaItem>,
    currentlyPlayingId: String?,
    isPlaying: Boolean,
    onPlayAudio: (MediaItem) -> Unit,
    onStartDownload: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenHostMedia: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGenre by remember { mutableStateOf("All") }
    var sortBy by remember { mutableStateOf("Popular") } // "Popular", "Title", "Duration"

    val genres = listOf("All", "Synthwave", "Electronic", "Chillhop", "Acoustic", "Private Audio")

    val audioTracks = musicItems.filter { it.type == MediaType.AUDIO }
        .filter { selectedGenre == "All" || it.genre.equals(selectedGenre, ignoreCase = true) }
        .let { list ->
            when (sortBy) {
                "Title" -> list.sortedBy { it.title }
                "Duration" -> list.sortedByDescending { it.durationSeconds }
                else -> list.sortedByDescending { it.playCount }
            }
        }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("music_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Music Header & Hosting Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF13112E),
                                Color(0xFF0B192A),
                                Color(0xFF1E1035)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(ElectricViolet.copy(alpha = 0.5f), NeonCyan.copy(alpha = 0.5f))),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Headphones, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "STUDIO MASTER AUDIO ENGINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = NeonCyan
                            )
                        }

                        Text("FLAC 96kHz / 24-bit", fontSize = 11.sp, color = NeonEmerald, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Music Vault & High-Res Streaming",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Text(
                        text = "Zero compression, hardware DSP equalizer, and real-time offline caching.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(NeonCyan, ElectricViolet)))
                                .clickable { onOpenHostMedia() }
                                .padding(horizontal = 16.dp, vertical = 9.dp)
                                .testTag("host_audio_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Host Audio File", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x26FFFFFF))
                                .clickable {
                                    val randomTrack = audioTracks.randomOrNull()
                                    if (randomTrack != null) onPlayAudio(randomTrack)
                                }
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shuffle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Shuffle Play", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // Genre Filter Chips
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(genres) { genre ->
                    FilterChip(
                        selected = selectedGenre == genre,
                        onClick = { selectedGenre = genre },
                        label = { Text(genre) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricViolet.copy(alpha = 0.25f),
                            selectedLabelColor = ElectricViolet
                        )
                    )
                }
            }
        }

        // Sorting & Count Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tracks (${audioTracks.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Sort, contentDescription = "Sort", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    listOf("Popular", "Title", "Duration").forEach { sort ->
                        Text(
                            text = sort,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (sortBy == sort) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (sortBy == sort) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { sortBy = sort }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Track List
        items(audioTracks) { track ->
            val isCurrent = track.id == currentlyPlayingId
            MediaListItem(
                item = track,
                onClick = { onPlayAudio(track) },
                onStartDownload = { onStartDownload(track.id) },
                onToggleFavorite = { onToggleFavorite(track.id) },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .then(
                        if (isCurrent) {
                            Modifier.border(1.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        } else Modifier
                    )
            )
        }
    }
}
