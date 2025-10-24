package com.moi.band.ui.test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moi.band.data.model.Track
import com.moi.band.player.PlayerViewModel

/**
 * Тестовый экран для проверки плеера
 */
@Composable
fun TestPlayerScreen(
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎸 Test Audio Player",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                // Тестовый MP3 с интернета (бесплатный)
                val testUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                
                val testTrack = Track(
                    id = 999,
                    title = "Test Song",
                    description = "Testing ExoPlayer",
                    coverImage = null,
                    audioUrl = testUrl,
                    duration = 180,
                    durationFormatted = "3:00",
                    views = 0,
                    albumTitle = "Test Album",
                    createdAt = ""
                )
                
                android.util.Log.d("TestPlayerScreen", "Playing test track: $testUrl")
                playerViewModel.playTrack(testTrack, testUrl)
            }
        ) {
            Text("Play Test MP3")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Если это работает - проблема в URL с сервера",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
