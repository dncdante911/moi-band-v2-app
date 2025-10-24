package com.moi.band.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.moi.band.MainActivity
import com.moi.band.R
import com.moi.band.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicPlayerService : MediaSessionService() {

    private var exoPlayer: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private var currentTrackTitle: String = "Master of Illusion"
    private var currentAlbumTitle: String = ""
    private var currentCoverUrl: String? = null
    private var isPlaying = false

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
        const val EXTRA_TRACK_COVER = "EXTRA_TRACK_COVER"
        const val EXTRA_SEEK_POSITION = "EXTRA_SEEK_POSITION"

        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "music_player_channel"
        private const val MEDIA_SESSION_TAG = "MoiBandPlayer"
    }

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("MusicPlayerService", "=== onCreate: Service created ===")

        createNotificationChannel()

        try {
            exoPlayer = ExoPlayer.Builder(this).build()

            mediaSession = MediaSession.Builder(this, exoPlayer!!)
                .setId(MEDIA_SESSION_TAG)
                .build()

            android.util.Log.d("MusicPlayerService", "✅ ExoPlayer и MediaSession инициализированы")

            exoPlayer?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                    super.onIsPlayingChanged(isPlayingNow)
                    isPlaying = isPlayingNow
                    android.util.Log.d("MusicPlayerService", "▶ onIsPlayingChanged: $isPlayingNow")
                    updateNotification(isPlayingNow)
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    super.onPlayerError(error)
                    android.util.Log.e("MusicPlayerService", "❌ Player error: ${error.message}", error)
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
                    android.util.Log.d("MusicPlayerService", "🎵 Playback state: $state")

                    if (playbackState == Player.STATE_ENDED) {
                        android.util.Log.d("MusicPlayerService", "⏹ Трек закончился, останавливаем")
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                }
            })
        } catch (e: Exception) {
            android.util.Log.e("MusicPlayerService", "❌ Failed to initialize ExoPlayer", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: "NO_ACTION"
        android.util.Log.d("MusicPlayerService", "🔄 onStartCommand: action=$action")

        when (intent?.action) {
            ACTION_PLAY -> {
                val url = intent.getStringExtra(EXTRA_TRACK_URL)
                currentTrackTitle = intent.getStringExtra(EXTRA_TRACK_TITLE) ?: "Unknown Track"
                currentAlbumTitle = intent.getStringExtra(EXTRA_ALBUM_TITLE) ?: ""
                currentCoverUrl = intent.getStringExtra(EXTRA_TRACK_COVER)

                android.util.Log.d("MusicPlayerService", "🎸 ACTION_PLAY: url=$url")
                android.util.Log.d("MusicPlayerService", "📸 Cover: $currentCoverUrl")

                if (url != null) {
                    playTrack(url)
                } else {
                    android.util.Log.d("MusicPlayerService", "▶ Resume from pause")
                    exoPlayer?.play()
                }
            }

            ACTION_PAUSE -> {
                android.util.Log.d("MusicPlayerService", "⏸ ACTION_PAUSE")
                exoPlayer?.pause()
                updateNotification(false)
            }

            ACTION_STOP -> {
                android.util.Log.d("MusicPlayerService", "🛑 ACTION_STOP")
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }

            ACTION_SEEK -> {
                val position = intent.getLongExtra(EXTRA_SEEK_POSITION, 0L)
                android.util.Log.d("MusicPlayerService", "⏩ ACTION_SEEK: $position")
                exoPlayer?.seekTo(position)
            }

            ACTION_NEXT -> {
                android.util.Log.d("MusicPlayerService", "⏭ ACTION_NEXT")
            }

            ACTION_PREVIOUS -> {
                android.util.Log.d("MusicPlayerService", "⏮ ACTION_PREVIOUS")
            }
        }

        return START_STICKY
    }

    private fun playTrack(url: String) {
        android.util.Log.d("MusicPlayerService", "▶ playTrack: $url")

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

            android.util.Log.d("MusicPlayerService", "📢 Creating notification...")
            val notification = createNotification(true)

            android.util.Log.d("MusicPlayerService", "🚀 Calling startForeground with NOTIFICATION_ID=$NOTIFICATION_ID")
            startForeground(NOTIFICATION_ID, notification)

            android.util.Log.d("MusicPlayerService", "✅ Foreground started successfully!")
        } catch (e: Exception) {
            android.util.Log.e("MusicPlayerService", "❌ Error playing track", e)
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        android.util.Log.d("MusicPlayerService", "📢 Creating notification channel...")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Music playback controls"
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableVibration(false)
                setSound(null, null)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            android.util.Log.d("MusicPlayerService", "✅ Notification channel created")
        }
    }

    private fun createNotification(isPlayingNow: Boolean): Notification {
        android.util.Log.d("MusicPlayerService", "📝 Creating notification: isPlaying=$isPlayingNow")

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (isPlayingNow) {
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

        val previousAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_media_previous,
            "Previous",
            createPendingIntent(ACTION_PREVIOUS)
        ).build()

        val nextAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_media_next,
            "Next",
            createPendingIntent(ACTION_NEXT)
        ).build()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentTrackTitle)
            .setContentText(currentAlbumTitle)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(contentIntent)
            .setDeleteIntent(createPendingIntent(ACTION_STOP))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setOngoing(isPlayingNow)
            .setColorized(true)
            .setColor(0xFFD700.toInt())

            .addAction(previousAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .addAction(stopAction)

        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(1, 2)

        builder.setStyle(style)

        if (!currentCoverUrl.isNullOrEmpty()) {
            android.util.Log.d("MusicPlayerService", "📸 Loading cover: $currentCoverUrl")
            loadAndSetLargeIcon(builder, currentCoverUrl!!)
        }

        android.util.Log.d("MusicPlayerService", "✅ Notification created")
        return builder.build()
    }

    private fun loadAndSetLargeIcon(builder: NotificationCompat.Builder, coverUrl: String) {
        android.util.Log.d("MusicPlayerService", "🖼 loadAndSetLargeIcon: $coverUrl")

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val imageLoader = ImageLoader(this@MusicPlayerService)
                val request = ImageRequest.Builder(this@MusicPlayerService)
                    .data(coverUrl)
                    .size(512, 512)
                    .build()

                val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
                if (result is BitmapDrawable) {
                    val bitmap: Bitmap? = result.bitmap
                    builder.setLargeIcon(bitmap)
                    android.util.Log.d("MusicPlayerService", "✅ Cover loaded: ${bitmap?.width}x${bitmap?.height}")

                    val notificationManager = getSystemService(NotificationManager::class.java)
                    notificationManager.notify(NOTIFICATION_ID, builder.build())
                } else {
                    android.util.Log.d("MusicPlayerService", "⚠ Not a BitmapDrawable")
                }
            } catch (e: Exception) {
                android.util.Log.e("MusicPlayerService", "❌ Failed to load cover", e)
            }
        }
    }

    private fun updateNotification(isPlayingNow: Boolean) {
        android.util.Log.d("MusicPlayerService", "🔄 updateNotification: isPlaying=$isPlayingNow")

        val notification = createNotification(isPlayingNow)
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

    override fun onGetSession(controllerInfo: androidx.media3.session.MediaSession.ControllerInfo) =
        mediaSession

    override fun onDestroy() {
        super.onDestroy()
        android.util.Log.d("MusicPlayerService", "🛑 onDestroy: Service destroyed")
        mediaSession?.release()
        mediaSession = null
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        android.util.Log.d("MusicPlayerService", "❌ onTaskRemoved: App closed")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}