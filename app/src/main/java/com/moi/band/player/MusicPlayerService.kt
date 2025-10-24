package com.moi.band.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.moi.band.MainActivity
import com.moi.band.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicPlayerService : Service() {

    private var exoPlayer: ExoPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private var currentTrackTitle: String = "Master of Illusion"
    private var currentAlbumTitle: String = ""

    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        const val ACTION_SEEK = "ACTION_SEEK"

        const val EXTRA_TRACK_URL = "EXTRA_TRACK_URL"
        const val EXTRA_TRACK_TITLE = "EXTRA_TRACK_TITLE"
        const val EXTRA_ALBUM_TITLE = "EXTRA_ALBUM_TITLE"
        const val EXTRA_SEEK_POSITION = "EXTRA_SEEK_POSITION"

        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "music_player_channel"
    }

    override fun onCreate() {
        super.onCreate()

        android.util.Log.d("MusicPlayerService", "onCreate: Service created")

        // Создаем Notification Channel
        createNotificationChannel()

        // Создаем MediaSession
        mediaSession = MediaSessionCompat(this, "MusicPlayerService").apply {
            isActive = true
        }

        // Инициализируем ExoPlayer
        try {
            exoPlayer = ExoPlayer.Builder(this).build()

            android.util.Log.d("MusicPlayerService", "ExoPlayer initialized")

            exoPlayer?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    android.util.Log.d("MusicPlayerService", "onIsPlayingChanged: $isPlaying")
                    updateNotification(isPlaying)
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    super.onPlayerError(error)
                    android.util.Log.e("MusicPlayerService", "Player error: ${error.message}", error)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    val state = when(playbackState) {
                        Player.STATE_IDLE -> "IDLE"
                        Player.STATE_BUFFERING -> "BUFFERING"
                        Player.STATE_READY -> "READY"
                        Player.STATE_ENDED -> "ENDED"
                        else -> "UNKNOWN"
                    }
                    android.util.Log.d("MusicPlayerService", "Playback state: $state")

                    // Когда трек закончился - останавливаем
                    if (playbackState == Player.STATE_ENDED) {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                }
            })
        } catch (e: Exception) {
            android.util.Log.e("MusicPlayerService", "Failed to initialize ExoPlayer", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        android.util.Log.d("MusicPlayerService", "onStartCommand: action=${intent?.action}")

        when (intent?.action) {
            ACTION_PLAY -> {
                val url = intent.getStringExtra(EXTRA_TRACK_URL)
                currentTrackTitle = intent.getStringExtra(EXTRA_TRACK_TITLE) ?: "Unknown Track"
                currentAlbumTitle = intent.getStringExtra(EXTRA_ALBUM_TITLE) ?: ""

                android.util.Log.d("MusicPlayerService", "ACTION_PLAY: url=$url")

                if (url != null) {
                    playTrack(url)
                } else {
                    exoPlayer?.play()
                }
            }

            ACTION_PAUSE -> {
                android.util.Log.d("MusicPlayerService", "ACTION_PAUSE")
                exoPlayer?.pause()
            }

            ACTION_STOP -> {
                android.util.Log.d("MusicPlayerService", "ACTION_STOP")
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }

            ACTION_SEEK -> {
                val position = intent.getLongExtra(EXTRA_SEEK_POSITION, 0L)
                exoPlayer?.seekTo(position)
            }
        }

        return START_STICKY
    }

    private fun playTrack(url: String) {
        android.util.Log.d("MusicPlayerService", "playTrack: $url")

        try {
            val mediaItem = MediaItem.Builder()
                .setUri(url)
                .setMimeType("audio/mpeg")
                .build()

            exoPlayer?.apply {
                setMediaItem(mediaItem)
                prepare()
                play()
            }

            // Показываем notification
            startForeground(NOTIFICATION_ID, createNotification(true))
        } catch (e: Exception) {
            android.util.Log.e("MusicPlayerService", "Error playing track", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(isPlaying: Boolean): Notification {
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action.Builder(
                R.drawable.ic_pause,
                "Pause",
                createPendingIntent(ACTION_PAUSE)
            ).build()
        } else {
            NotificationCompat.Action.Builder(
                R.drawable.ic_play,
                "Play",
                createPendingIntent(ACTION_PLAY)
            ).build()
        }

        val stopAction = NotificationCompat.Action.Builder(
            R.drawable.ic_stop,
            "Stop",
            createPendingIntent(ACTION_STOP)
        ).build()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentTrackTitle)
            .setContentText(currentAlbumTitle)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(contentIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession?.sessionToken)
                .setShowActionsInCompactView(0, 1))
            .addAction(playPauseAction)
            .addAction(stopAction)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setOngoing(isPlaying)
            .build()
    }

    private fun updateNotification(isPlaying: Boolean) {
        val notification = createNotification(isPlaying)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MusicPlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        android.util.Log.d("MusicPlayerService", "onDestroy: Service destroyed")
        exoPlayer?.release()
        exoPlayer = null
        mediaSession?.release()
        mediaSession = null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        android.util.Log.d("MusicPlayerService", "onTaskRemoved: App closed")
        // Останавливаем воспроизведение когда приложение закрыто
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}