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
import com.example.devcompanion.domain.repository.AuthRepository
import kotlinx.coroutines.flow.collectLatest

data class GitHubUiState(
    val query: String = "",
    val activeQuery: String = "",

    val language: String = "All",
    val sort: String = "Best match",

    val activeLanguage: String = "All",
    val activeSort: String = "Best match",

    val repositories: List<GitHubRepository> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val nextPage: Int? = null,
    val error: String? = null,
    val hasSearched: Boolean = false
)

data class MyRepositoriesUiState(
    val repositories: List<GitHubRepository> = emptyList(),

    val sort: String = "Recently updated",
    val activeSort: String = "Recently updated",

    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val nextPage: Int? = 1,
    val error: String? = null,
    val hasLoaded: Boolean = false
)

@HiltViewModel
class GitHubViewModel @Inject constructor(
    private val repository: GitHubRepositoryDataSource,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GitHubUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    private val _myRepositoriesState = MutableStateFlow(
        MyRepositoriesUiState()
    )

    val myRepositoriesState = _myRepositoriesState.asStateFlow()

    private var myRepositoriesJob: Job? = null

    val isAuthenticated = authRepository.isAuthenticatedFlow

    init {
        viewModelScope.launch {
            authRepository.isAuthenticatedFlow.collectLatest { authenticated ->
                if (!authenticated) {
                    clearMyRepositories()
                }
            }
        }
    }

    fun updateQuery(query: String) {
        _uiState.update {
            it.copy(query = query)
        }
    }

    fun updateLanguage(language: String) {
        _uiState.update {
            it.copy(language = language)
        }
    }

    fun updateSort(sort: String) {
        _uiState.update {
            it.copy(sort = sort)
        }
    }

    fun updateMyRepositoriesSort(sort: String) {
        if (_myRepositoriesState.value.sort == sort) return
        myRepositoriesJob?.cancel()
        _myRepositoriesState.update {
            it.copy(
                sort = sort,
                activeSort = sort,
                repositories = emptyList(),
                nextPage = 1,
                isLoading = false,
                isLoadingMore = false,
                error = null,
                hasLoaded = false
            )
        }
        loadMyRepositories()
    }

    fun searchRepositories() {
        val query = _uiState.value.query.trim()

        val language = _uiState.value.language
        val sort = _uiState.value.sort

        val apiLanguage = language.takeUnless { it == "All" }

        val apiSort = when (sort) {
            "Stars" -> "stars"
            "Recently updated" -> "updated"
            else -> null
        }


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
                hasSearched = true,
                activeLanguage = language,
                activeSort = sort,
            )
        }

        searchJob = viewModelScope.launch {
            try {
                val result = repository.searchRepositories(
                    query = query,
                    page = 1,
                    language = apiLanguage,
                    sort = apiSort
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
                val apiSort = when (current.activeSort) {
                    "Stars" -> "stars"
                    "Recently updated" -> "updated"
                    else -> null
                }
                val result = repository.searchRepositories(
                    query = current.activeQuery,
                    page = page,
                    language = current.activeLanguage.takeUnless { it == "All" },
                    sort = apiSort
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

    fun loadMyRepositories() {
        if (!authRepository.isAuthenticatedFlow.value) {
            return
        }
        val current = _myRepositoriesState.value

        if (current.isLoading || current.hasLoaded) {
            return
        }

        myRepositoriesJob?.cancel()

        _myRepositoriesState.update {
            it.copy(
                isLoading = true,
                isLoadingMore = false,
                error = null
            )
        }

        myRepositoriesJob = viewModelScope.launch {
            try {
                val activeSort = _myRepositoriesState.value.sort
                val apiSort = when (activeSort) {
                    "Name (A-Z)" -> "full_name"
                    "Recently created" -> "created"
                    "Recently pushed" -> "pushed"
                    else -> "updated"
                }
                val direction = if (activeSort == "Name (A-Z)") "asc" else "desc"
                val result = repository.getMyRepositories(
                    page = 1,
                    sort = apiSort,
                    direction = direction
                )

                _myRepositoriesState.update {
                    it.copy(
                        repositories = result.items,
                        nextPage = result.nextPage,
                        isLoading = false,
                        hasLoaded = true,
                        activeSort = activeSort
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _myRepositoriesState.update {
                    it.copy(
                        isLoading = false,
                        error = errorMessage(exception)
                    )
                }
            }
        }
    }

    fun loadMoreMyRepositories() {
        if (!authRepository.isAuthenticatedFlow.value) {
            return
        }
        val current = _myRepositoriesState.value
        val page = current.nextPage ?: return

        if (
            !current.hasLoaded ||
            current.isLoading ||
            current.isLoadingMore
        ) {
            return
        }

        _myRepositoriesState.update {
            it.copy(
                isLoadingMore = true,
                error = null
            )
        }

        myRepositoriesJob = viewModelScope.launch {
            try {
                val apiSort = when (current.activeSort) {
                    "Name (A-Z)" -> "full_name"
                    "Recently created" -> "created"
                    "Recently pushed" -> "pushed"
                    else -> "updated"
                }

                val direction = if (current.activeSort == "Name (A-Z)") {
                    "asc"
                } else {
                    "desc"
                }

                val result = repository.getMyRepositories(
                    page = page,
                    sort = apiSort,
                    direction = direction
                )

                _myRepositoriesState.update {
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
                _myRepositoriesState.update {
                    it.copy(
                        isLoadingMore = false,
                        error = errorMessage(exception))
                }
            }
        }
    }

    fun clearMyRepositories() {
        myRepositoriesJob?.cancel()
        myRepositoriesJob = null

        _myRepositoriesState.value = MyRepositoriesUiState()
    }
}