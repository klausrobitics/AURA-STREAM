package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald

@Composable
fun WaveformVisualizer(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    barColorStart: Color = NeonCyan,
    barColorEnd: Color = ElectricViolet,
    isPlaying: Boolean = true
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val totalBars = amplitudes.size.coerceAtLeast(1)
        val spacing = 6.dp.toPx()
        val availableWidth = size.width - (spacing * (totalBars - 1))
        val barWidth = (availableWidth / totalBars).coerceAtLeast(3.dp.toPx())
        val maxHeight = size.height

        val brush = Brush.verticalGradient(
            colors = listOf(barColorStart, barColorEnd, if (isPlaying) NeonEmerald else barColorEnd),
            startY = 0f,
            endY = maxHeight
        )

        amplitudes.forEachIndexed { index, amp ->
            val barHeight = (amp * maxHeight).coerceIn(4.dp.toPx(), maxHeight)
            val x = index * (barWidth + spacing)
            val y = (maxHeight - barHeight) / 2f

            drawRoundRect(
                brush = brush,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }
    }
}
