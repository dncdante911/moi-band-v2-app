package com.moi.band

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Главный класс приложения Master of Illusion
 * 
 * @HiltAndroidApp - инициализирует Hilt DI
 */
@HiltAndroidApp
class MoiApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        if (BuildConfig.DEBUG_MODE) {
            println("🎸 Master of Illusion запущен!")
            println("🌐 API URL: ${BuildConfig.API_BASE_URL}")
        }
    }
}
