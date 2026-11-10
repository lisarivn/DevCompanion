package com.example.devcompanion.data.repository

import com.example.devcompanion.data.mapper.toDomain
import com.example.devcompanion.data.remote.api.GitHubApiService
import com.example.devcompanion.domain.model.GitHubCommit
import com.example.devcompanion.domain.model.GitHubIssue
import com.example.devcompanion.domain.model.GitHubPullRequest
import com.example.devcompanion.domain.model.GitHubRepository
import com.example.devcompanion.domain.model.GitHubRepositoryDetails
import com.example.devcompanion.domain.model.PageResult
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import javax.inject.Inject
import com.example.devcompanion.data.remote.toPageResult
import com.example.devcompanion.domain.model.GitHubUser
import com.example.devcompanion.data.auth.TokenStorage
import com.example.devcompanion.domain.model.GitHubEvent
import com.example.devcompanion.domain.model.GitHubBranch

class GitHubRepositoryImpl @Inject constructor(
    private val apiService: GitHubApiService,
    private val tokenStorage: TokenStorage
) : GitHubRepositoryDataSource {

    override suspend fun searchRepositories(
        query: String,
        page: Int,
        language: String?,
        sort: String?,
        order: String
    ): PageResult<GitHubRepository> {

        val searchQuery = buildString {
            append(query.trim())

            if (!language.isNullOrBlank()) {
                append(" language:")
                append(language)
            }
        }

        return apiService.searchRepositories(
            query = searchQuery,
            sort = sort,
            order = order,
            page = page
        ).toPageResult { response ->
            response.items.map { it.toDomain() }
        }
    }

    override suspend fun getMyRepositories(
        page: Int,
        sort: String,
        direction: String
    ): PageResult<GitHubRepository> {
        val tokens = tokenStorage.getTokens()
            ?: throw IllegalStateException("User is not authenticated.")

        val expiresAt = tokens.accessTokenExpiresAt
            ?: throw IllegalStateException(
                "Access token has no expiration information."
            )

        if (System.currentTimeMillis() >= expiresAt) {
            throw IllegalStateException("Access token has expired.")
        }

        return apiService.getMyRepositories(
            authorization = "Bearer ${tokens.accessToken}",
            sort = sort,
            direction = direction,
            page = page
        ).toPageResult { repositories ->
            repositories.map { it.toDomain() }
        }
    }

    override suspend fun getRepositoryDetails(
        owner: String,
        repo: String
    ): GitHubRepositoryDetails {
        return apiService.getRepositoryDetails(owner, repo)
            .toDomain()
    }

    override suspend fun getIssues(
        owner: String,
        repo: String,
        page: Int,
        state: String
    ): PageResult<GitHubIssue> {
        return apiService.getIssues(
            owner = owner,
            repo = repo,
            state = state,
            page = page
        ).toPageResult { issues ->
            issues
                .filter { it.pullRequest == null }
                .map { it.toDomain() }
        }
    }

    override suspend fun getIssueDetails(
        owner: String,
        repo: String,
        issueNumber: Int
    ): GitHubIssue {
        return apiService.getIssueDetails(
            owner = owner,
            repo = repo,
            issueNumber = issueNumber
        ).toDomain()
    }


    override suspend fun getPullRequests(
        owner: String,
        repo: String,
        page: Int,
        state: String
    ): PageResult<GitHubPullRequest> {
        return apiService.getPullRequests(
            owner = owner,
            repo = repo,
            state = state,
            page = page
        ).toPageResult { pulls ->
            pulls.map { it.toDomain() }
        }
    }

    override suspend fun getPullRequestDetails(
        owner: String,
        repo: String,
        pullNumber: Int
    ): GitHubPullRequest {
        return apiService.getPullRequestDetails(
            owner = owner,
            repo = repo,
            pullNumber = pullNumber
        ).toDomain()
    }

    override suspend fun getCommits(
        owner: String,
        repo: String,
        page: Int,
        branch: String?
    ): PageResult<GitHubCommit> {
        return apiService.getCommits(
            owner = owner,
            repo = repo,
            branch = branch,
            page = page
        ).toPageResult { commits ->
            commits.map { it.toDomain() }
        }
    }


    override suspend fun getCommitDetails(
        owner: String,
        repo: String,
        sha: String
    ): GitHubCommit {
        return apiService.getCommitDetails(
            owner = owner,
            repo = repo,
            sha = sha
        ).toDomain()
    }


    override suspend fun getBranches(
        owner: String,
        repo: String
    ): List<GitHubBranch> {
        val response = apiService.getBranches(
            owner = owner,
            repo = repo
        )

        if (!response.isSuccessful) {
            throw retrofit2.HttpException(response)
        }

        return response.body()
            .orEmpty()
            .map { branch ->
                GitHubBranch(name = branch.name)
            }
    }


    override suspend fun getUserProfile(username: String): GitHubUser {
        return apiService.getUserProfile(username).toDomain()
    }

    override suspend fun getAuthenticatedUser(): GitHubUser {
        val tokens = tokenStorage.getTokens()
            ?: throw IllegalStateException("User is not authenticated.")

        val expiresAt = tokens.accessTokenExpiresAt
            ?: throw IllegalStateException("Access token has no expiration information.")

        if (System.currentTimeMillis() >= expiresAt) {
            throw IllegalStateException("Access token has expired.")
        }

        return apiService.getAuthenticatedUser(
            authorization = "Bearer ${tokens.accessToken}"
        ).toDomain()
    }

    override suspend fun getPublicEvents(
        username: String
    ): List<GitHubEvent> {
        return apiService.getPublicEvents(username)
            .map { it.toDomain() }
    }
}