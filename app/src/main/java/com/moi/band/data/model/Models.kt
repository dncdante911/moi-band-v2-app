package com.moi.band.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// ==================== API RESPONSES ====================

/**
 * Базовый ответ API
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("validation_errors") val validationErrors: Map<String, String>? = null,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("version") val version: String
)

/**
 * Пагинированный ответ
 */
data class PaginatedResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T,
    @SerializedName("pagination") val pagination: Pagination
)

data class Pagination(
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("has_next") val hasNext: Boolean,
    @SerializedName("has_prev") val hasPrev: Boolean
)

// ==================== AUTH ====================

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirm") val passwordConfirm: String,
    @SerializedName("display_name") val displayName: String? = null
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("user") val user: User
)

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("avatar_path") val avatarPath: String?
)

// ==================== ALBUMS ====================

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("coverImagePath") val coverImagePath: String,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("track_count") val trackCount: Int = 0,
    @SerializedName("total_duration") val totalDuration: Int? = null,
    @SerializedName("total_duration_formatted") val totalDurationFormatted: String? = null,
    @SerializedName("createdAt") val createdAt: String
)

// ==================== TRACKS ====================

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("cover_image") val coverImage: String?,
    @SerializedName("audio_url") val audioUrl: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("duration_formatted") val durationFormatted: String?,
    @SerializedName("views") val views: Int = 0,
    @SerializedName("albumTitle") val albumTitle: String? = null,
    @SerializedName("created_at") val createdAt: String
)

data class AlbumDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("coverImagePath") val coverImagePath: String,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("track_count") val trackCount: Int,
    @SerializedName("total_duration") val totalDuration: Int?,
    @SerializedName("total_duration_formatted") val totalDurationFormatted: String?,
    @SerializedName("tracks") val tracks: List<Track>
)

// ==================== NEWS ====================

@Entity(tableName = "news")
data class News(
    @PrimaryKey
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content_preview") val contentPreview: String?,
    @SerializedName("category") val category: String,
    @SerializedName("image") val image: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("created_at_time") val createdAtTime: String?
)

data class NewsDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("category") val category: String,
    @SerializedName("image") val image: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("created_at_full") val createdAtFull: String,
    @SerializedName("created_at_time") val createdAtTime: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("navigation") val navigation: NewsNavigation?
)

data class NewsNavigation(
    @SerializedName("prev") val prev: NewsNavigationItem?,
    @SerializedName("next") val next: NewsNavigationItem?
)

data class NewsNavigationItem(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String
)

// ==================== GALLERY ====================

@Entity(tableName = "gallery")
data class GalleryImage(
    @PrimaryKey
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("thumbnail_url") val thumbnailUrl: String,
    @SerializedName("created_at") val createdAt: String
)

data class GalleryResponse(
    @SerializedName("images") val images: List<GalleryImage>,
    @SerializedName("categories") val categories: Map<String, String>
)

// ==================== CHAT ====================

data class ChatRoom(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("description") val description: String?,
    @SerializedName("icon") val icon: String?,
    @SerializedName("is_private") val isPrivate: Boolean,
    @SerializedName("messages_count") val messagesCount: Int,
    @SerializedName("last_message") val lastMessage: LastMessage?
)

data class LastMessage(
    @SerializedName("text") val text: String,
    @SerializedName("username") val username: String,
    @SerializedName("created_at") val createdAt: String
)

data class ChatMessage(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("avatar_path") val avatarPath: String?,
    @SerializedName("message") val message: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("is_edited") val isEdited: Boolean,
    @SerializedName("is_own_message") val isOwnMessage: Boolean = false
)

data class ChatMessagesResponse(
    @SerializedName("room") val room: ChatRoomInfo,
    @SerializedName("messages") val messages: List<ChatMessage>,
    @SerializedName("total") val total: Int
)

data class ChatRoomInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class SendMessageRequest(
    @SerializedName("room_id") val roomId: Int,
    @SerializedName("message") val message: String
)

// ==================== USER PROFILE ====================

data class UserProfile(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("avatar_path") val avatarPath: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("status") val status: String,
    @SerializedName("is_admin") val isAdmin: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("last_seen") val lastSeen: String?,
    @SerializedName("theme") val theme: String,
    @SerializedName("notifications_enabled") val notificationsEnabled: Boolean,
    @SerializedName("email_notifications") val emailNotifications: Boolean,
    @SerializedName("statistics") val statistics: UserStatistics
)

data class UserStatistics(
    @SerializedName("plays") val plays: Int,
    @SerializedName("room_messages") val roomMessages: Int,
    @SerializedName("favorites") val favorites: Int
)

// ==================== PLAYER ====================

data class PlayTrackRequest(
    @SerializedName("track_id") val trackId: Int
)

data class LyricsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("lyrics") val lyrics: String?,
    @SerializedName("source") val source: String
)

// ==================== ENUMS ====================

enum class NewsCategory(val value: String, val displayName: String) {
    ALL("", "Все"),
    RELEASE("release", "Релизы"),
    EVENT("event", "События"),
    UPDATE("update", "Обновления")
}

enum class GalleryCategory(val value: String, val displayName: String) {
    ALL("", "Все"),
    STUDIO("studio", "Студия"),
    CONCERT("concert", "Концерты"),
    EVENT("event", "События"),
    PROMO("promo", "Промо")
}

enum class Theme(val value: String, val displayName: String) {
    POWER_METAL("dark", "⚔️ Power Metal"),
    HEAVY_METAL("light", "🤘 Heavy Metal"),
    PUNK_ROCK("punk", "🎸 Punk Rock")
}
