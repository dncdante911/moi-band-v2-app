package com.moi.band

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.moi.band.ui.navigation.NavGraph
import com.moi.band.ui.theme.MoiBandTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 🎸 MASTER OF ILLUSION - MainActivity
 * 
 * Главная активность приложения с Jetpack Compose UI
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash Screen
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        setContent {
            MoiBandTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
