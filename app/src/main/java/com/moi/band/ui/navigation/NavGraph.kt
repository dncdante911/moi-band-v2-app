package com.moi.band.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moi.band.ui.auth.AuthViewModel
import com.moi.band.ui.auth.LoginScreen
import com.moi.band.ui.auth.RegisterScreen
import com.moi.band.ui.main.MainScreen
import kotlinx.coroutines.launch

/**
 * 🧭 NavGraph - главная навигация приложения
 * 
 * Стартовый экран: MainScreen (Новости)
 * Login/Register показываются только при нажатии на Профиль
 */
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        // Main Screen (with bottom navigation) - СТАРТОВЫЙ ЭКРАН
        composable("main") {
            MainScreen(
                onNavigateToLogin = {
                    navController.navigate("login")
                }
            )
        }
        
        // Login Screen
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.popBackStack()
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Register Screen
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack("main", inclusive = false)
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
