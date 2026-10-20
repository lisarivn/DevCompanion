
package com.example.devcompanion.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devcompanion.domain.model.FavoriteRepository
import com.example.devcompanion.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DashboardUiState(
    val savedRepositoriesCount: Int = 0,
    val recentFavorites: List<FavoriteRepository> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    favoritesRepository: FavoritesRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> =
        favoritesRepository.getAllFavorites()
            .map { favorites ->
                DashboardUiState(
                    savedRepositoriesCount = favorites.size,
                    recentFavorites = favorites.take(3)
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DashboardUiState()
            )
}
