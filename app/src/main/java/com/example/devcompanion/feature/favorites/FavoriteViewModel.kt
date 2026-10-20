package com.example.devcompanion.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devcompanion.domain.model.FavoriteRepository
import com.example.devcompanion.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteRepository>> =
        favoritesRepository.getAllFavorites()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val favoritesCount: StateFlow<Int> =
        favoritesRepository.getFavoritesCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )

    fun isFavorite(repositoryId: Long) =
        favoritesRepository.isFavorite(repositoryId)

    fun addFavorite(repository: FavoriteRepository) {
        viewModelScope.launch {
            favoritesRepository.addFavorite(repository)
        }
    }

    fun removeFavorite(repositoryId: Long) {
        viewModelScope.launch {
            favoritesRepository.removeFavorite(repositoryId)
        }
    }

    fun toggleFavorite(repository: FavoriteRepository, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyFavorite) {
                favoritesRepository.removeFavorite(repository.id)
            } else {
                favoritesRepository.addFavorite(repository)
            }
        }
    }
}