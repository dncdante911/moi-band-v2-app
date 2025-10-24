package com.moi.band.data.repository

import com.moi.band.data.api.ApiService
import com.moi.band.data.model.*
import com.moi.band.util.Resource
import com.moi.band.util.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🎸 Base Repository - общие функции для всех репозиториев
 */
abstract class BaseRepository {
    
    /**
     * Безопасный API вызов с обработкой ошибок
     */
    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): Resource<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Unknown error")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }
}

// ==================== AUTH REPOSITORY ====================

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager
) : BaseRepository() {
    
    /**
     * Логин
     */
    suspend fun login(username: String, password: String): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            api.login(LoginRequest(username, password))
        }
        
        when (result) {
            is Resource.Success -> {
                result.data?.data?.let { authResponse ->
                    // Сохраняем токен и user info
                    tokenManager.saveToken(authResponse.token)
                    tokenManager.saveUserId(authResponse.user.id)
                    tokenManager.saveUsername(authResponse.user.username)
                    
                    emit(Resource.Success(authResponse))
                } ?: emit(Resource.Error("No data received"))
            }
            is Resource.Error -> emit(Resource.Error(result.message ?: "Login failed"))
            else -> {}
        }
    }
    
    /**
     * Регистрация
     */
    suspend fun register(
        username: String,
        email: String,
        password: String,
        passwordConfirm: String
    ): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            api.register(
                RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    passwordConfirm = passwordConfirm
                )
            )
        }
        
        when (result) {
            is Resource.Success -> {
                result.data?.data?.let { authResponse ->
                    // Сохраняем токен и user info
                    tokenManager.saveToken(authResponse.token)
                    tokenManager.saveUserId(authResponse.user.id)
                    tokenManager.saveUsername(authResponse.user.username)
                    
                    emit(Resource.Success(authResponse))
                } ?: emit(Resource.Error("No data received"))
            }
            is Resource.Error -> emit(Resource.Error(result.message ?: "Registration failed"))
            else -> {}
        }
    }
    
    /**
     * Logout
     */
    suspend fun logout() {
        tokenManager.clearAll()
    }
    
    /**
     * Проверка авторизации
     */
    suspend fun isLoggedIn(): Boolean {
        return tokenManager.hasToken()
    }
}

// ==================== ALBUM REPOSITORY ====================

@Singleton
class AlbumRepository @Inject constructor(
    private val api: ApiService
) : BaseRepository() {
    
    /**
     * Получить список альбомов
     */
    suspend fun getAlbums(page: Int = 1, perPage: Int = 10): Flow<Resource<List<Album>>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            api.getAlbums(page, perPage)
        }
        
        when (result) {
            is Resource.Success -> {
                val albums = result.data?.data ?: emptyList()
                emit(Resource.Success(albums))
            }
            is Resource.Error -> emit(Resource.Error(result.message ?: "Failed to load albums"))
            else -> {}
        }
    }
    
    /**
     * Получить детали альбома
     */
    suspend fun getAlbumDetail(albumId: Int): Flow<Resource<AlbumDetail>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            api.getAlbumDetail(albumId)
        }
        
        when (result) {
            is Resource.Success -> {
                result.data?.data?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Album not found"))
            }
            is Resource.Error -> emit(Resource.Error(result.message ?: "Failed to load album"))
            else -> {}
        }
    }
}

// ==================== NEWS REPOSITORY ====================

@Singleton
class NewsRepository @Inject constructor(
    private val api: ApiService
) : BaseRepository() {
    
    /**
     * Получить список новостей
     */
    suspend fun getNews(
        page: Int = 1,
        perPage: Int = 10,
        category: String? = null
    ): Flow<Resource<List<News>>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            api.getNews(page, perPage, category)
        }
        
        when (result) {
            is Resource.Success -> {
                val news = result.data?.data ?: emptyList()
                emit(Resource.Success(news))
            }
            is Resource.Error -> emit(Resource.Error(result.message ?: "Failed to load news"))
            else -> {}
        }
    }
    
    /**
     * Получить детали новости
     */
    suspend fun getNewsDetail(newsId: Int): Flow<Resource<NewsDetail>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            api.getNewsDetail(newsId)
        }
        
        when (result) {
            is Resource.Success -> {
                result.data?.data?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("News not found"))
            }
            is Resource.Error -> emit(Resource.Error(result.message ?: "Failed to load news"))
            else -> {}
        }
    }
}

// ==================== GALLERY REPOSITORY ====================

@Singleton
class GalleryRepository @Inject constructor(
    private val api: ApiService
) : BaseRepository() {
    
    /**
     * Получить галерею
     */
    suspend fun getGallery(
        page: Int = 1,
        perPage: Int = 12,
        category: String? = null
    ): Flow<Resource<GalleryResponse>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            api.getGallery(page, perPage, category)
        }
        
        when (result) {
            is Resource.Success -> {
                result.data?.data?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("No images found"))
            }
            is Resource.Error -> emit(Resource.Error(result.message ?: "Failed to load gallery"))
            else -> {}
        }
    }
}

// ==================== CHAT REPOSITORY ====================

@Singleton
class ChatRepository @Inject constructor(
    private val api: ApiService
) : BaseRepository() {
    
    /**
     * Получить список комнат
     */
    suspend fun getChatRooms(): Flow<Resource<List<ChatRoom>>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            api.getChatRooms()
        }
        
        when (result) {
            is Resource.Success -> {
                result.data?.data?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("No chat rooms found"))
            }
            is Resource.Error -> emit(Resource.Error(result.message ?: "Failed to load chat rooms"))
            else -> {}
        }
    }
    
    /**
     * Получить сообщения комнаты
     */
    suspend fun getChatMessages(
        roomId: Int,
        limit: Int = 50,
        offset: Int = 0
    ): Flow<Resource<ChatMessagesResponse>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            api.getChatMessages(roomId, limit, offset)
        }
        
        when (result) {
            is Resource.Success -> {
                result.data?.data?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("No messages found"))
            }
            is Resource.Error -> emit(Resource.Error(result.message ?: "Failed to load messages"))
            else -> {}
        }
    }
    
    /**
     * Отправить сообщение
     */
    suspend fun sendMessage(roomId: Int, message: String): Flow<Resource<ChatMessage>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            api.sendMessage(SendMessageRequest(roomId, message))
        }
        
        when (result) {
            is Resource.Success -> {
                result.data?.data?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Failed to send message"))
            }
            is Resource.Error -> emit(Resource.Error(result.message ?: "Failed to send message"))
            else -> {}
        }
    }
}

// ==================== USER REPOSITORY ====================

@Singleton
class UserRepository @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager
) : BaseRepository() {
    
    /**
     * Получить профиль пользователя
     */
    suspend fun getUserProfile(): Flow<Resource<UserProfile>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            api.getUserProfile()
        }
        
        when (result) {
            is Resource.Success -> {
                result.data?.data?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Profile not found"))
            }
            is Resource.Error -> emit(Resource.Error(result.message ?: "Failed to load profile"))
            else -> {}
        }
    }
    
    /**
     * Обновить тему
     */
    suspend fun updateTheme(theme: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        
        val result = safeApiCall {
            api.updateUserProfile(mapOf("theme" to theme))
        }
        
        when (result) {
            is Resource.Success -> {
                tokenManager.saveTheme(theme)
                emit(Resource.Success(true))
            }
            is Resource.Error -> emit(Resource.Error(result.message ?: "Failed to update theme"))
            else -> {}
        }
    }
}
