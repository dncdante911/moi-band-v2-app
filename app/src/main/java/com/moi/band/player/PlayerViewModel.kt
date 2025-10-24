package com.moi.band.player

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moi.band.data.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    private val _playlist = MutableStateFlow<List<Track>>(emptyList())
    val playlist: StateFlow<List<Track>> = _playlist.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private var progressUpdateJob: Job? = null

    /**
     * Играть трек
     */
    fun playTrack(track: Track, fullAudioUrl: String, playlist: List<Track> = emptyList()) {
        android.util.Log.d("PlayerViewModel", "playTrack: ${track.title}, URL: $fullAudioUrl")

        _currentTrack.value = track
        _isPlaying.value = true
        _showPlayer.value = true
        _playlist.value = playlist
        _duration.value = (track.duration * 1000L) // секунды в миллисекунды
        _currentPosition.value = 0L

        // Запускаем обновление прогресса
        startProgressUpdates()

        // Запрашиваем разрешение на уведомления (Android 13+)
        requestNotificationPermission()

        // Формируем полный URL для обложки
        val fullCoverUrl = when {
            track.coverImage.isNullOrEmpty() -> null
            track.coverImage!!.startsWith("http") -> track.coverImage
            track.coverImage!!.startsWith("/") -> "${com.moi.band.BuildConfig.BASE_URL}${track.coverImage}"
            else -> "${com.moi.band.BuildConfig.BASE_URL}/${track.coverImage}"
        }

        android.util.Log.d("PlayerViewModel", "Cover URL: $fullCoverUrl")

        // Запускаем service с полной информацией
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_PLAY
            putExtra(MusicPlayerService.EXTRA_TRACK_URL, fullAudioUrl)
            putExtra(MusicPlayerService.EXTRA_TRACK_TITLE, track.title)
            putExtra(MusicPlayerService.EXTRA_ALBUM_TITLE, track.albumTitle ?: "")
            putExtra(MusicPlayerService.EXTRA_TRACK_COVER, fullCoverUrl)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            android.util.Log.d("PlayerViewModel", "Service started successfully with cover")
        } catch (e: Exception) {
            android.util.Log.e("PlayerViewModel", "Failed to start service", e)
        }
    }

    /**
     * Играть трек из плейлиста
     */
    fun playTrackFromPlaylist(track: Track) {
        val index = _playlist.value.indexOf(track)
        if (index != -1) {
            val fullUrl = "${com.moi.band.BuildConfig.BASE_URL}${track.audioUrl}"
            playTrack(track, fullUrl, _playlist.value)
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
        stopProgressUpdates()

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
        startProgressUpdates()

        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_PLAY
        }
        context.startService(intent)
    }

    /**
     * Следующий трек
     */
    fun next() {
        val current = _currentTrack.value ?: return
        val list = _playlist.value
        if (list.isEmpty()) return

        val currentIndex = list.indexOf(current)
        val nextIndex = (currentIndex + 1) % list.size
        val nextTrack = list[nextIndex]

        val fullUrl = "${com.moi.band.BuildConfig.BASE_URL}${nextTrack.audioUrl}"
        playTrack(nextTrack, fullUrl, list)
    }

    /**
     * Предыдущий трек
     */
    fun previous() {
        val current = _currentTrack.value ?: return
        val list = _playlist.value
        if (list.isEmpty()) return

        val currentIndex = list.indexOf(current)
        val prevIndex = if (currentIndex - 1 < 0) list.size - 1 else currentIndex - 1
        val prevTrack = list[prevIndex]

        val fullUrl = "${com.moi.band.BuildConfig.BASE_URL}${prevTrack.audioUrl}"
        playTrack(prevTrack, fullUrl, list)
    }

    /**
     * Перемотка
     */
    fun seekTo(position: Long) {
        _currentPosition.value = position

        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_SEEK
            putExtra(MusicPlayerService.EXTRA_SEEK_POSITION, position)
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
        _playlist.value = emptyList()
        _currentPosition.value = 0L
        stopProgressUpdates()

        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_STOP
        }
        context.startService(intent)
    }

    /**
     * Обновление текущей позиции
     */
    fun updatePosition(position: Long) {
        _currentPosition.value = position
    }

    /**
     * Обновление длительности
     */
    fun updateDuration(duration: Long) {
        _duration.value = duration
    }

    /**
     * Запуск периодического обновления прогресса
     */
    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressUpdateJob = viewModelScope.launch {
            while (_isPlaying.value) {
                delay(1000)
                if (_currentPosition.value < _duration.value) {
                    _currentPosition.value += 1000
                }
            }
        }
    }

    /**
     * Остановка обновления прогресса
     */
    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    /**
     * Скрыть мини-плеер
     */
    fun hidePlayer() {
        _showPlayer.value = false
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressUpdates()
    }

    /**
     * Запрос разрешения на уведомления (Android 13+)
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val permission = android.Manifest.permission.POST_NOTIFICATIONS
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                if (!hasPermission) {
                    android.util.Log.d("PlayerViewModel", "Notification permission not granted")
                } else {
                    android.util.Log.d("PlayerViewModel", "Notification permission granted")
                }
            } catch (e: Exception) {
                android.util.Log.e("PlayerViewModel", "Error checking notification permission", e)
            }
        }
    }
}