package com.moi.band.data.api

import com.moi.band.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * 🎸 MASTER OF ILLUSION - API Service
 * 
 * Все endpoints для работы с API https://moi-band.com.ua/api/v1/
 */
interface ApiService {
    
    // ==================== AUTH ====================
    
    @POST("auth/login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthResponse>>
    
    @POST("auth/register.php")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<AuthResponse>>
    
    // ==================== ALBUMS ====================
    
    @GET("albums/list.php")
    suspend fun getAlbums(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10
    ): Response<PaginatedResponse<List<Album>>>
    
    @GET("albums/detail.php")
    suspend fun getAlbumDetail(
        @Query("id") albumId: Int
    ): Response<ApiResponse<AlbumDetail>>
    
    // ==================== TRACKS ====================
    
    @GET("tracks/list.php")
    suspend fun getTracks(
        @Query("album_id") albumId: Int,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<PaginatedResponse<Map<String, Any>>>
    
    @GET("tracks/search.php")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<PaginatedResponse<List<Track>>>
    
    @POST("tracks/play.php")
    suspend fun playTrack(
        @Body request: PlayTrackRequest
    ): Response<ApiResponse<Track>>
    
    // ==================== NEWS ====================
    
    @GET("news/list.php")
    suspend fun getNews(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Query("category") category: String? = null
    ): Response<PaginatedResponse<List<News>>>
    
    @GET("news/detail.php")
    suspend fun getNewsDetail(
        @Query("id") newsId: Int
    ): Response<ApiResponse<NewsDetail>>
    
    // ==================== GALLERY ====================
    
    @GET("gallery/list.php")
    suspend fun getGallery(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 12,
        @Query("category") category: String? = null
    ): Response<PaginatedResponse<GalleryResponse>>
    
    // ==================== CHAT ====================
    
    @GET("chat/rooms.php")
    suspend fun getChatRooms(): Response<ApiResponse<List<ChatRoom>>>
    
    @GET("chat/messages.php")
    suspend fun getChatMessages(
        @Query("room_id") roomId: Int,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<ApiResponse<ChatMessagesResponse>>
    
    @POST("chat/send.php")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): Response<ApiResponse<ChatMessage>>
    
    // ==================== USER PROFILE ====================
    
    @GET("users/profile.php")
    suspend fun getUserProfile(): Response<ApiResponse<UserProfile>>
    
    @PUT("users/profile.php")
    suspend fun updateUserProfile(
        @Body profile: Map<String, Any>
    ): Response<ApiResponse<UserProfile>>
    
    // ==================== PLAYER ====================
    
    @GET("player/lyrics.php")
    suspend fun getLyrics(
        @Query("track_id") trackId: Int
    ): Response<LyricsResponse>
    
    @GET("player/queue.php")
    suspend fun getQueue(
        @Query("album_id") albumId: Int? = null,
        @Query("playlist_id") playlistId: Int? = null
    ): Response<ApiResponse<Map<String, Any>>>
}
