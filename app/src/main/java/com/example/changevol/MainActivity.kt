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

        setContent {
            MaterialTheme {
                Surface(color = Color(0xFF121212)) {
                    VolumeScreen(
                        onSetVolume = { percent ->
                            setVolume(percent)
                            finish()
                        }
                    )
                }
            }
        }
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
fun VolumeScreen(onSetVolume: (Float) -> Unit) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)          // <- вот тут регулируешь высоту кнопок
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            VolumeButton("30", listOf(Color(0xFF66BB6A), Color(0xFF388E3C)), Modifier.weight(1f), onSetVolume)
            VolumeButton("40", listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)), Modifier.weight(1f), onSetVolume)
            VolumeButton("50", listOf(Color(0xFF42A5F5), Color(0xFF1565C0)), Modifier.weight(1f), onSetVolume)
            VolumeButton("60", listOf(Color(0xFFEC407A), Color(0xFFAD1457)), Modifier.weight(1f), onSetVolume)
            VolumeButton("75", listOf(Color(0xFFEF5350), Color(0xFFB71C1C)), Modifier.weight(1f), onSetVolume)
        }
    }
}

@Composable
fun VolumeButton(
    text: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
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
                    colors = gradientColors,
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