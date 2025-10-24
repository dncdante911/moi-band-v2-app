package com.moi.band.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moi.band.data.model.Album
import com.moi.band.data.model.AlbumDetail
import com.moi.band.data.repository.AlbumRepository
import com.moi.band.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 📀 AlbumViewModel - логика работы с альбомами
 */
@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val albumRepository: AlbumRepository
) : ViewModel() {
    
    // ==================== ALBUMS LIST ====================
    
    private val _albumsState = MutableStateFlow<Resource<List<Album>>?>(null)
    val albumsState: StateFlow<Resource<List<Album>>?> = _albumsState.asStateFlow()
    
    private var currentPage = 1
    private var isLoadingMore = false
    
    /**
     * Загрузить список альбомов
     */
    fun loadAlbums(refresh: Boolean = false) {
        if (refresh) {
            currentPage = 1
        }
        
        if (isLoadingMore && !refresh) return
        
        isLoadingMore = true
        
        viewModelScope.launch {
            albumRepository.getAlbums(page = currentPage, perPage = 10).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val currentAlbums = if (refresh || currentPage == 1) {
                            emptyList()
                        } else {
                            (_albumsState.value as? Resource.Success)?.data ?: emptyList()
                        }
                        
                        val newAlbums = currentAlbums + (result.data ?: emptyList())
                        _albumsState.value = Resource.Success(newAlbums)
                        
                        if (result.data?.isNotEmpty() == true) {
                            currentPage++
                        }
                        
                        isLoadingMore = false
                    }
                    is Resource.Error -> {
                        _albumsState.value = result
                        isLoadingMore = false
                    }
                    is Resource.Loading -> {
                        if (currentPage == 1) {
                            _albumsState.value = result
                        }
                    }
                }
            }
        }
    }
    
    // ==================== ALBUM DETAIL ====================
    
    private val _albumDetailState = MutableStateFlow<Resource<AlbumDetail>?>(null)
    val albumDetailState: StateFlow<Resource<AlbumDetail>?> = _albumDetailState.asStateFlow()
    
    /**
     * Загрузить детали альбома
     */
    fun loadAlbumDetail(albumId: Int) {
        viewModelScope.launch {
            albumRepository.getAlbumDetail(albumId).collect { result ->
                _albumDetailState.value = result
            }
        }
    }
    
    /**
     * Сбросить состояние деталей
     */
    fun resetAlbumDetail() {
        _albumDetailState.value = null
    }
}
