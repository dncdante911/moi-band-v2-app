package com.moi.band.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moi.band.data.model.Album
import com.moi.band.data.model.GalleryImage
import com.moi.band.data.model.News
import com.moi.band.data.model.Track

/**
 * 🗄️ Room Database для оффлайн кэширования
 */
@Database(
    entities = [
        Album::class,
        Track::class,
        News::class,
        GalleryImage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    // DAOs будут добавлены при необходимости
    // abstract fun albumDao(): AlbumDao
    // abstract fun trackDao(): TrackDao
    // abstract fun newsDao(): NewsDao
    // abstract fun galleryDao(): GalleryDao
}
