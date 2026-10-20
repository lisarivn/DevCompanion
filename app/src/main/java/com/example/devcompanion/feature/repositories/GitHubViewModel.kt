package com.example.devcompanion.feature.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devcompanion.domain.model.GitHubRepository
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class GitHubUiState(
    val query: String = "",
    val activeQuery: String = "",
    val repositories: List<GitHubRepository> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val nextPage: Int? = null,
    val error: String? = null,
    val hasSearched: Boolean = false
)

@HiltViewModel
class GitHubViewModel @Inject constructor(
    private val repository: GitHubRepositoryDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(GitHubUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun updateQuery(query: String) {
        _uiState.update {
            it.copy(query = query)
        }
    }

    fun searchRepositories() {
        val query = _uiState.value.query.trim()

        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    error = "Enter a repository name or keyword."
                )
            }
            return
        }

        searchJob?.cancel()

        _uiState.update {
            it.copy(
                activeQuery = query,
                repositories = emptyList(),
                nextPage = 1,
                isLoading = true,
                isLoadingMore = false,
                error = null,
                hasSearched = true
            )
        }

        searchJob = viewModelScope.launch {
            try {
                val result = repository.searchRepositories(
                    query = query,
                    page = 1
                )

                _uiState.update {
                    it.copy(
                        repositories = result.items,
                        nextPage = result.nextPage,
                        isLoading = false
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = errorMessage(exception)
                    )
                }
            }
        }
    }

    fun loadMoreRepositories() {
        val current = _uiState.value
        val page = current.nextPage ?: return

        if (
            current.isLoading ||
            current.isLoadingMore ||
            current.activeQuery.isBlank()
        ) {
            return
        }

        // Оновлюємо стан до запуску coroutine,
        // щоб уникнути подвійного натискання Load more.
        _uiState.update {
            it.copy(
                isLoadingMore = true,
                error = null
            )
        }

        searchJob = viewModelScope.launch {
            try {
                val result = repository.searchRepositories(
                    query = current.activeQuery,
                    page = page
                )

                _uiState.update {
                    it.copy(
                        repositories = (
                                it.repositories + result.items
                                ).distinctBy { repository -> repository.id },
                        nextPage = result.nextPage,
                        isLoadingMore = false
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        error = errorMessage(exception)
                    )
                }
            }
        }
    }

    private fun errorMessage(exception: Exception): String {
        return when (exception) {
            is HttpException -> when (exception.code()) {
                403, 429 ->
                    "GitHub rate limit reached. Try again later."

                422 ->
                    "Invalid search query."

                else ->
                    "GitHub API error: ${exception.code()}"
            }

            is IOException ->
                "Network error. Check your internet connection."

            else ->
                "Unable to load repositories."
        }
    }
}