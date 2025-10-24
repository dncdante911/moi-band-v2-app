# 🎵 AUDIO PLAYER GUIDE - Master of Illusion

**Полное руководство по музыкальному плееру на ExoPlayer**

---

## 📦 Архитектура плеера

### Компоненты

```
Player System
├── MusicPlayerService.kt          # Foreground Service (фоновое воспроизведение)
├── MusicPlayerManager.kt          # Управление ExoPlayer
├── PlayerViewModel.kt             # UI логика
├── MiniPlayer.kt                  # Компактный плеер
└── FullPlayerScreen.kt            # Полноэкранный плеер
```

---

## 🎸 Возможности плеера

✅ **Фоновое воспроизведение** - Музыка играет даже при свернутом приложении  
✅ **Notification Controls** - Управление из уведомлений  
✅ **Shuffle & Repeat** - Случайный порядок и повтор  
✅ **Seek (перемотка)** - Перемотка трека вперед/назад  
✅ **Queue Management** - Очередь воспроизведения  
✅ **Track History** - История прослушиваний  
✅ **Lyrics Display** - Отображение текстов песен  

---

## 🔧 Как работает

### 1. MusicPlayerService

Foreground Service для фонового воспроизведения:

```kotlin
@AndroidEntryPoint
class MusicPlayerService : Service() {
    
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSession
    
    override fun onCreate() {
        super.onCreate()
        
        // Инициализация ExoPlayer
        exoPlayer = ExoPlayer.Builder(this)
            .build()
        
        // Media Session для уведомлений
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .build()
        
        // Foreground notification
        startForeground(NOTIFICATION_ID, createNotification())
    }
    
    fun playTrack(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }
}
```

### 2. MusicPlayerManager

Управление плеером через Hilt:

```kotlin
@Singleton
class MusicPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private var serviceIntent: Intent? = null
    private var serviceBound = false
    
    fun playTrack(track: Track) {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = ACTION_PLAY
            putExtra(EXTRA_TRACK_URL, BuildConfig.BASE_URL + track.audioUrl)
            putExtra(EXTRA_TRACK_TITLE, track.title)
        }
        context.startForegroundService(intent)
    }
    
    fun pause() {
        sendAction(ACTION_PAUSE)
    }
    
    fun resume() {
        sendAction(ACTION_PLAY)
    }
}
```

### 3. PlayerViewModel

UI State Management:

```kotlin
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerManager: MusicPlayerManager
) : ViewModel() {
    
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val playerState = _playerState.asStateFlow()
    
    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack = _currentTrack.asStateFlow()
    
    fun playTrack(track: Track) {
        playerManager.playTrack(track)
        _currentTrack.value = track
        _playerState.value = PlayerState.Playing
    }
    
    fun togglePlayPause() {
        when (_playerState.value) {
            PlayerState.Playing -> {
                playerManager.pause()
                _playerState.value = PlayerState.Paused
            }
            PlayerState.Paused -> {
                playerManager.resume()
                _playerState.value = PlayerState.Playing
            }
            else -> {}
        }
    }
}
```

---

## 🎨 UI Components

### MiniPlayer

Компактный плеер внизу экрана:

```kotlin
@Composable
fun MiniPlayer(
    track: Track?,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onExpand: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onExpand),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Cover
            AsyncImage(
                model = BuildConfig.BASE_URL + track?.coverImage,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            
            // Track Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = track?.title ?: "Не играет",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track?.albumTitle ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Controls
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null
                )
            }
            
            IconButton(onClick = onNext) {
                Icon(Icons.Default.SkipNext, null)
            }
        }
    }
}
```

### FullPlayerScreen

Полноэкранный плеер:

```kotlin
@Composable
fun FullPlayerScreen(
    track: Track,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Close button
        IconButton(onClick = onClose) {
            Icon(Icons.Default.ExpandMore, null)
        }
        
        Spacer(Modifier.height(32.dp))
        
        // Large Album Cover
        AsyncImage(
            model = BuildConfig.BASE_URL + track.coverImage,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        )
        
        Spacer(Modifier.height(32.dp))
        
        // Track Title
        Text(
            text = track.title,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 2
        )
        
        Text(
            text = track.albumTitle ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(Modifier.height(24.dp))
        
        // Progress Slider
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { onSeek(it.toLong()) },
            valueRange = 0f..duration.toFloat()
        )
        
        // Time labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(currentPosition))
            Text(formatTime(duration))
        }
        
        Spacer(Modifier.height(24.dp))
        
        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious) {
                Icon(Icons.Default.SkipPrevious, null, Modifier.size(48.dp))
            }
            
            IconButton(onClick = onPlayPause) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    null,
                    Modifier.size(64.dp)
                )
            }
            
            IconButton(onClick = onNext) {
                Icon(Icons.Default.SkipNext, null, Modifier.size(48.dp))
            }
        }
    }
}

fun formatTime(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / (1000 * 60)) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
```

---

## 📲 Notification Controls

Управление из уведомлений Android:

```kotlin
private fun createNotification(): Notification {
    return NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle(currentTrack?.title)
        .setContentText(currentTrack?.albumTitle)
        .setSmallIcon(R.drawable.ic_music_note)
        .setLargeIcon(loadAlbumArt())
        .addAction(R.drawable.ic_previous, "Previous", previousPendingIntent)
        .addAction(
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
            if (isPlaying) "Pause" else "Play",
            playPausePendingIntent
        )
        .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
        .setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSession.sessionToken)
        )
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()
}
```

---

## 🔄 Очередь воспроизведения

```kotlin
class PlayQueue {
    private val queue = mutableListOf<Track>()
    private var currentIndex = 0
    
    fun addTrack(track: Track) {
        queue.add(track)
    }
    
    fun addAlbum(tracks: List<Track>) {
        queue.clear()
        queue.addAll(tracks)
        currentIndex = 0
    }
    
    fun next(): Track? {
        if (currentIndex < queue.size - 1) {
            currentIndex++
            return queue[currentIndex]
        }
        return null
    }
    
    fun previous(): Track? {
        if (currentIndex > 0) {
            currentIndex--
            return queue[currentIndex]
        }
        return null
    }
    
    fun shuffle() {
        val currentTrack = queue[currentIndex]
        queue.shuffle()
        currentIndex = queue.indexOf(currentTrack)
    }
}
```

---

## 💾 Оффлайн скачивание

```kotlin
@Singleton
class DownloadManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    suspend fun downloadTrack(track: Track): Result<File> {
        return withContext(Dispatchers.IO) {
            try {
                val url = BuildConfig.BASE_URL + track.audioUrl
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                
                if (response.isSuccessful) {
                    val file = File(context.filesDir, "music/${track.id}.mp3")
                    file.parentFile?.mkdirs()
                    
                    response.body?.byteStream()?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    
                    Result.success(file)
                } else {
                    Result.failure(Exception("Download failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    fun isDownloaded(trackId: Int): Boolean {
        val file = File(context.filesDir, "music/$trackId.mp3")
        return file.exists()
    }
}
```

---

## 📝 Отображение текстов

```kotlin
@Composable
fun LyricsSheet(
    trackId: Int,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val lyrics by viewModel.lyricsState.collectAsState()
    
    LaunchedEffect(trackId) {
        viewModel.loadLyrics(trackId)
    }
    
    ModalBottomSheet(
        onDismissRequest = { /* close */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "📝 ТЕКСТ ПЕСНИ",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(Modifier.height(16.dp))
            
            when (lyrics) {
                is Resource.Success -> {
                    val lyricsText = (lyrics as Resource.Success).data?.lyrics
                    if (lyricsText != null) {
                        Text(
                            text = lyricsText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text("Текста нет 😢")
                    }
                }
                is Resource.Error -> {
                    Text("Ошибка загрузки текста")
                }
                is Resource.Loading -> {
                    CircularProgressIndicator()
                }
                else -> {}
            }
        }
    }
}
```

---

## 🎯 Best Practices

1. **Используй MediaSession** для интеграции с Android Audio Focus
2. **Обрабатывай ошибки** при загрузке треков (нет интернета, 404)
3. **Кэшируй треки** для быстрой загрузки
4. **Освобождай ресурсы** при закрытии приложения
5. **Показывай прогресс** загрузки оффлайн треков

---

**Готово! Рок дальше! 🤘🎸**
