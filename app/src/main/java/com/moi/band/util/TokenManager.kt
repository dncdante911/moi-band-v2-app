package com.moi.band.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🔐 TokenManager - управление JWT токенами
 * 
 * Использует DataStore для безопасного хранения токенов
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "moi_band_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val THEME_KEY = stringPreferencesKey("theme")
    }
    
    // ==================== JWT TOKEN ====================
    
    /**
     * Сохранить JWT токен
     */
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
        }
    }
    
    /**
     * Получить JWT токен
     */
    suspend fun getToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[JWT_TOKEN_KEY]
        }.first()
    }
    
    /**
     * Flow для наблюдения за токеном
     */
    val tokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[JWT_TOKEN_KEY]
    }
    
    /**
     * Проверить, есть ли токен
     */
    suspend fun hasToken(): Boolean {
        return getToken() != null
    }
    
    /**
     * Удалить токен (logout)
     */
    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(JWT_TOKEN_KEY)
        }
    }
    
    // ==================== USER INFO ====================
    
    /**
     * Сохранить ID пользователя
     */
    suspend fun saveUserId(userId: Int) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }
    
    /**
     * Получить ID пользователя
     */
    suspend fun getUserId(): Int? {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }.first()
    }
    
    /**
     * Сохранить username
     */
    suspend fun saveUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }
    
    /**
     * Получить username
     */
    suspend fun getUsername(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[USERNAME_KEY]
        }.first()
    }
    
    // ==================== THEME ====================
    
    /**
     * Сохранить тему
     */
    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
    
    /**
     * Получить тему
     */
    suspend fun getTheme(): String {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: "dark"
        }.first()
    }
    
    /**
     * Flow для наблюдения за темой
     */
    val themeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: "dark"
    }
    
    // ==================== CLEAR ALL ====================
    
    /**
     * Очистить все данные (полный logout)
     */
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
