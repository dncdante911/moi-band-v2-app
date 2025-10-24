package com.moi.band.util

/**
 * Resource wrapper для обработки состояний загрузки данных
 * 
 * Используется для UI состояний: Loading -> Success/Error
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
