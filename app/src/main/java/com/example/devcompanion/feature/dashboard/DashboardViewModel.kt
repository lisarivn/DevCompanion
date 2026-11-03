
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
import com.example.devcompanion.domain.model.GitHubEvent
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val savedRepositoriesCount: Int = 0,
    val recentFavorites: List<FavoriteRepository> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    favoritesRepository: FavoritesRepository,
    private val githubRepository: GitHubRepositoryDataSource
) : ViewModel() {

    private val _events = MutableStateFlow<List<GitHubEvent>>(emptyList())
    val events = _events.asStateFlow()

    private val _eventsLoading = MutableStateFlow(false)
    val eventsLoading = _eventsLoading.asStateFlow()

    private val _eventsError = MutableStateFlow<String?>(null)
    val eventsError = _eventsError.asStateFlow()

    private var eventsJob: Job? = null

    fun loadEvents(username: String) {
        eventsJob?.cancel()

        _events.value = emptyList()
        _eventsError.value = null
        _eventsLoading.value = true

        eventsJob = viewModelScope.launch {
            try {
                _events.value = githubRepository.getPublicEvents(username)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _eventsError.value = "Unable to load GitHub activity."
            } finally {
                _eventsLoading.value = false
            }
        }
    }

    fun clearEvents() {
        eventsJob?.cancel()
        _events.value = emptyList()
        _eventsError.value = null
        _eventsLoading.value = false
    }

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
