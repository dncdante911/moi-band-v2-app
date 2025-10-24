package com.moi.band.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moi.band.BuildConfig
import com.moi.band.ui.albums.AlbumDetailScreen
import com.moi.band.ui.screens.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToLogin: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()

    val authViewModel: com.moi.band.ui.auth.AuthViewModel = hiltViewModel()
    val playerViewModel: com.moi.band.player.PlayerViewModel = hiltViewModel()

    var isLoggedIn by remember { mutableStateOf(false) }
    var showFullPlayer by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoggedIn = authViewModel.isLoggedIn()
    }

    // Диалог полного плеера
    if (showFullPlayer) {
        com.moi.band.player.FullPlayerScreen(
            viewModel = playerViewModel,
            onDismiss = { showFullPlayer = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🎸 MASTER OF ILLUSION",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Column {
                // Mini Player
                com.moi.band.player.MiniPlayer(
                    viewModel = playerViewModel,
                    onExpandClick = { showFullPlayer = true }
                )

                // Navigation Bar
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                if (item.route == "profile" && !isLoggedIn) {
                                    onNavigateToLogin()
                                    return@NavigationBarItem
                                }

                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "news",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("news") {
                NewsScreen()
            }

            composable("albums") {
                AlbumsScreen(
                    onAlbumClick = { albumId ->
                        navController.navigate("album_detail/$albumId")
                    }
                )
            }

            composable(
                route = "album_detail/{albumId}",
                arguments = listOf(
                    navArgument("albumId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val albumId = backStackEntry.arguments?.getInt("albumId") ?: 0

                AlbumDetailScreen(
                    albumId = albumId,
                    onBack = { navController.popBackStack() },
                    onTrackClick = { track, allTracks ->
                        android.util.Log.d("MainScreen", "Track clicked: ${track.title}")
                        android.util.Log.d("MainScreen", "Original audioUrl: ${track.audioUrl}")

                        // Формируем правильный URL
                        val fullAudioUrl = when {
                            track.audioUrl.startsWith("http://") ||
                                    track.audioUrl.startsWith("https://") -> {
                                track.audioUrl
                            }
                            track.audioUrl.startsWith("/") -> {
                                "${BuildConfig.BASE_URL}${track.audioUrl}"
                            }
                            else -> {
                                "${BuildConfig.BASE_URL}/${track.audioUrl}"
                            }
                        }

                        android.util.Log.d("MainScreen", "Full audio URL: $fullAudioUrl")

                        playerViewModel.playTrack(track, fullAudioUrl, allTracks)
                    }
                )
            }

            composable("gallery") {
                GalleryScreen()
            }

            composable("chat") {
                if (isLoggedIn) {
                    ChatScreen()
                } else {
                    RequiresAuthScreen(
                        feature = "Чат",
                        onLoginClick = onNavigateToLogin
                    )
                }
            }

            composable("profile") {
                if (isLoggedIn) {
                    ProfileScreen(
                        onLogout = {
                            scope.launch {
                                val tokenManager = com.moi.band.util.TokenManager(navController.context)
                                tokenManager.clearAll()
                                isLoggedIn = false
                                navController.navigate("news") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                } else {
                    RequiresAuthScreen(
                        feature = "Профиль",
                        onLoginClick = onNavigateToLogin
                    )
                }
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem("news", Icons.Default.Newspaper, "Новости"),
    BottomNavItem("albums", Icons.Default.Album, "Альбомы"),
    BottomNavItem("gallery", Icons.Default.PhotoLibrary, "Галерея"),
    BottomNavItem("chat", Icons.Default.Chat, "Чат"),
    BottomNavItem("profile", Icons.Default.Person, "Профиль")
)