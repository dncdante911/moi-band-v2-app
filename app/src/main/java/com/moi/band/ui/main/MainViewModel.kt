package com.moi.band.ui.main

import androidx.lifecycle.ViewModel
import com.moi.band.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * MainViewModel для доступа к AuthRepository в MainScreen
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel()
