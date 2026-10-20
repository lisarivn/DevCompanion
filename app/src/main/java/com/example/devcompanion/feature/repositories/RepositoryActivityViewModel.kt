package com.example.devcompanion.feature.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devcompanion.domain.model.GitHubCommit
import com.example.devcompanion.domain.model.GitHubIssue
import com.example.devcompanion.domain.model.GitHubPullRequest
import com.example.devcompanion.domain.model.PageResult
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class ActivityUiState<T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val nextPage: Int? = 1,
    val error: String? = null,
    val repositoryKey: String? = null,
    val hasLoaded: Boolean = false
)

@HiltViewModel
class RepositoryActivityViewModel @Inject constructor(
    private val repository: GitHubRepositoryDataSource
) : ViewModel() {

    private val _issues =
        MutableStateFlow(ActivityUiState<GitHubIssue>())
    val issues = _issues.asStateFlow()

    private val _pullRequests =
        MutableStateFlow(ActivityUiState<GitHubPullRequest>())
    val pullRequests = _pullRequests.asStateFlow()

    private val _commits =
        MutableStateFlow(ActivityUiState<GitHubCommit>())
    val commits = _commits.asStateFlow()

    private var issuesJob: Job? = null
    private var pullsJob: Job? = null
    private var commitsJob: Job? = null

    private fun repositoryKey(owner: String, repo: String): String {
        return "$owner/$repo"
    }

    private fun errorMessage(exception: Exception): String {
        return when (exception) {
            is HttpException -> when (exception.code()) {
                401 -> "Authentication required."
                403, 429 -> "GitHub API rate limit reached."
                404 -> "Repository not found."
                422 -> "Invalid GitHub API request."
                else -> "GitHub API error: ${exception.code()}"
            }

            is IOException ->
                "Network error. Check your internet connection."

            else -> "Unable to load data."
        }
    }

    private fun <T> prepareState(
        state: MutableStateFlow<ActivityUiState<T>>,
        owner: String,
        repo: String,
        currentJob: Job?,
        cancelJob: () -> Unit
    ) {
        val key = repositoryKey(owner, repo)

        if (state.value.repositoryKey != key) {
            currentJob?.cancel()
            cancelJob()

            state.value = ActivityUiState(
                repositoryKey = key
            )
        }
    }

    private fun <T> loadPage(
        state: MutableStateFlow<ActivityUiState<T>>,
        owner: String,
        repo: String,
        maxPagesPerLoad: Int = 1,
        loader: suspend (Int) -> PageResult<T>,
        itemId: (T) -> Any,
        setJob: (Job) -> Unit
    ) {
        val current = state.value
        val key = repositoryKey(owner, repo)

        if (current.repositoryKey != key) return

        val firstPage = current.nextPage ?: return

        if (current.isLoading || current.isLoadingMore) return

        state.value = current.copy(
            isLoading = !current.hasLoaded,
            isLoadingMore = current.hasLoaded,
            error = null
        )

        val job = viewModelScope.launch {
            var page: Int? = firstPage
            var loadedPages = 0

            try {
                while (page != null && loadedPages < maxPagesPerLoad) {
                    val result = loader(page)
                    loadedPages++

                    // Захист від результату застарілого запиту.
                    if (state.value.repositoryKey != key) return@launch

                    val combined = (
                            state.value.items + result.items
                            ).distinctBy(itemId)

                    state.value = state.value.copy(
                        items = combined,
                        nextPage = result.nextPage,
                        hasLoaded = true,
                        isLoading = false,
                        isLoadingMore = true
                    )

                    page = result.nextPage

                    // Якщо знайшли записи, зупиняємо
                    // автоматичне завантаження.
                    if (result.items.isNotEmpty()) break
                }

                if (state.value.repositoryKey == key) {
                    state.value = state.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        hasLoaded = true
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (state.value.repositoryKey == key) {
                    state.value = state.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = errorMessage(exception)
                    )
                }
            }
        }

        setJob(job)
    }

    // ---------- ISSUES ----------

    fun initializeIssues(owner: String, repo: String) {
        prepareState(
            state = _issues,
            owner = owner,
            repo = repo,
            currentJob = issuesJob,
            cancelJob = { issuesJob = null }
        )

        if (!_issues.value.hasLoaded &&
            !_issues.value.isLoading &&
            !_issues.value.isLoadingMore
        ) {
            loadIssues(owner, repo)
        }
    }

    fun loadIssues(owner: String, repo: String) {
        loadPage(
            state = _issues,
            owner = owner,
            repo = repo,
            maxPagesPerLoad = 5,
            loader = { page ->
                repository.getIssues(owner, repo, page)
            },
            itemId = { it.id },
            setJob = { issuesJob = it }
        )
    }

    // ---------- PULL REQUESTS ----------

    fun initializePullRequests(owner: String, repo: String) {
        prepareState(
            state = _pullRequests,
            owner = owner,
            repo = repo,
            currentJob = pullsJob,
            cancelJob = { pullsJob = null }
        )

        if (!_pullRequests.value.hasLoaded &&
            !_pullRequests.value.isLoading &&
            !_pullRequests.value.isLoadingMore
        ) {
            loadPullRequests(owner, repo)
        }
    }

    fun loadPullRequests(owner: String, repo: String) {
        loadPage(
            state = _pullRequests,
            owner = owner,
            repo = repo,
            loader = { page ->
                repository.getPullRequests(owner, repo, page)
            },
            itemId = { it.id },
            setJob = { pullsJob = it }
        )
    }

    // ---------- COMMITS ----------

    fun initializeCommits(owner: String, repo: String) {
        prepareState(
            state = _commits,
            owner = owner,
            repo = repo,
            currentJob = commitsJob,
            cancelJob = { commitsJob = null }
        )

        if (!_commits.value.hasLoaded &&
            !_commits.value.isLoading &&
            !_commits.value.isLoadingMore
        ) {
            loadCommits(owner, repo)
        }
    }

    fun loadCommits(owner: String, repo: String) {
        loadPage(
            state = _commits,
            owner = owner,
            repo = repo,
            loader = { page ->
                repository.getCommits(owner, repo, page)
            },
            itemId = { it.sha },
            setJob = { commitsJob = it }
        )
    }
}