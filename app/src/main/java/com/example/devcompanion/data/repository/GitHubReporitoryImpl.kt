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

class GitHubRepositoryImpl @Inject constructor(
    private val apiService: GitHubApiService
) : GitHubRepositoryDataSource {

    override suspend fun searchRepositories(
        query: String,
        page: Int
    ): PageResult<GitHubRepository> {
        return apiService.searchRepositories(
            query = query,
            page = page
        ).toPageResult { response ->
            response.items.map { it.toDomain() }
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
        page: Int
    ): PageResult<GitHubIssue> {
        return apiService.getIssues(
            owner = owner,
            repo = repo,
            page = page
        ).toPageResult { issues ->
            issues
                .filter { it.pullRequest == null }
                .map { it.toDomain() }
        }
    }

    override suspend fun getPullRequests(
        owner: String,
        repo: String,
        page: Int
    ): PageResult<GitHubPullRequest> {
        return apiService.getPullRequests(
            owner = owner,
            repo = repo,
            page = page
        ).toPageResult { pulls ->
            pulls.map { it.toDomain() }
        }
    }

    override suspend fun getCommits(
        owner: String,
        repo: String,
        page: Int
    ): PageResult<GitHubCommit> {
        return apiService.getCommits(
            owner = owner,
            repo = repo,
            page = page
        ).toPageResult { commits ->
            commits.map { it.toDomain() }
        }
    }
}