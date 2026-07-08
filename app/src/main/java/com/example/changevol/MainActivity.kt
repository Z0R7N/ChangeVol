package com.example.changevol

import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private lateinit var audio: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audio = getSystemService(AUDIO_SERVICE) as AudioManager

        // текущая громкость в процентах (0f..1f)
        val currentPercent = getCurrentVolumePercent()

        setContent {
            MaterialTheme {
                Surface(color = Color(0xFF121212)) {
                    VolumeScreen(
                        currentPercent = currentPercent,
                        onSetVolume = { percent ->
                            setVolume(percent)
                            finish()
                        }
                    )
                }
            }
        }
    }

    private fun getCurrentVolumePercent(): Float {
        val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val current = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (max == 0) return 0f
        return current.toFloat() / max.toFloat()
    }

    private fun setVolume(percent: Float) {
        val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val value = (max * percent).toInt()

        audio.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            value,
            0
        )
    }
}

@Composable
fun VolumeScreen(currentPercent: Float, onSetVolume: (Float) -> Unit) {

    // значения кнопок в процентах
    val buttonValues = listOf(30, 40, 50, 60, 75)

    // находим ближайшее значение к текущей громкости
    val currentPercentInt = (currentPercent * 100).toInt()
    val closestValue = buttonValues.minByOrNull { kotlin.math.abs(it - currentPercentInt) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            VolumeButton("30", listOf(Color(0xFF15A4A4), Color(0xFF0DA8A8)), Modifier.weight(1f), 30 == closestValue, onSetVolume)
            VolumeButton("40", listOf(Color(0xFF4CAF50), Color(0xFF40A945)), Modifier.weight(1f), 40 == closestValue, onSetVolume)
            VolumeButton("50", listOf(Color(0xFF42A5F5), Color(0xFF3287E8)), Modifier.weight(1f), 50 == closestValue, onSetVolume)
            VolumeButton("60", listOf(Color(0xFFCE6E47), Color(0xFFCE6E47)), Modifier.weight(1f), 60 == closestValue, onSetVolume)
            VolumeButton("75", listOf(Color(0xFFEF5350), Color(0xFFE84545)), Modifier.weight(1f), 75 == closestValue, onSetVolume)
        }
    }
}

@Composable
fun VolumeButton(
    text: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    onClick: (Float) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        label = "buttonScale"
    )
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 10f,
        label = "buttonElevation"
    )

    // если кнопка активна (ближайшая к текущей громкости) - серый градиент вместо обычного
    val colors = if (isActive) {
        listOf(Color(0xFF9E9E9E), Color(0xFF9F9F9F))
    } else {
        gradientColors
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .scale(scale)
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = colors,
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick(text.toFloat() / 100f)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$text%",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
}