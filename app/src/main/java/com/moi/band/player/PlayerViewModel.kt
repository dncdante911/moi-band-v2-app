package com.moi.band.player

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.moi.band.data.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * 🎵 PlayerViewModel - управление плеером из UI
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _showPlayer = MutableStateFlow(false)
    val showPlayer: StateFlow<Boolean> = _showPlayer.asStateFlow()
    
    /**
     * Играть трек
     */
    fun playTrack(track: Track, fullAudioUrl: String) {
        android.util.Log.d("PlayerViewModel", "playTrack: ${track.title}, URL: $fullAudioUrl")
        
        _currentTrack.value = track
        _isPlaying.value = true
        _showPlayer.value = true
        
        // Запускаем service
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_PLAY
            putExtra(MusicPlayerService.EXTRA_TRACK_URL, fullAudioUrl)
            putExtra(MusicPlayerService.EXTRA_TRACK_TITLE, track.title)
            putExtra(MusicPlayerService.EXTRA_ALBUM_TITLE, track.albumTitle ?: "")
        }
        
        try {
            context.startForegroundService(intent)
            android.util.Log.d("PlayerViewModel", "Service started successfully")
        } catch (e: Exception) {
            android.util.Log.e("PlayerViewModel", "Failed to start service", e)
        }
    }
    
    /**
     * Toggle Play/Pause
     */
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            resume()
        }
    }
    
    /**
     * Пауза
     */
    fun pause() {
        _isPlaying.value = false
        
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_PAUSE
        }
        context.startService(intent)
    }
    
    /**
     * Возобновить
     */
    fun resume() {
        _isPlaying.value = true
        
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_PLAY
        }
        context.startService(intent)
    }
    
    /**
     * Остановить
     */
    fun stop() {
        _isPlaying.value = false
        _showPlayer.value = false
        _currentTrack.value = null
        
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_STOP
        }
        context.startService(intent)
    }
    
    /**
     * Скрыть мини-плеер
     */
    fun hidePlayer() {
        _showPlayer.value = false
    }
}
