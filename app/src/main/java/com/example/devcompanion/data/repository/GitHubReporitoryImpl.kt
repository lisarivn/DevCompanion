package com.example.devcompanion.data.repository

import com.example.devcompanion.data.mapper.toDomain
import com.example.devcompanion.data.remote.api.GitHubApiService
import com.example.devcompanion.domain.model.GitHubRepository
import com.example.devcompanion.domain.model.GitHubRepositoryDetails
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import javax.inject.Inject
import com.example.devcompanion.domain.model.GitHubIssue
import com.example.devcompanion.domain.model.GitHubPullRequest
import com.example.devcompanion.domain.model.GitHubCommit

class GitHubRepositoryImpl @Inject constructor(
    private val apiService: GitHubApiService
) : GitHubRepositoryDataSource {

    override suspend fun searchRepositories(
        query: String
    ): List<GitHubRepository> {
        return apiService.searchRepositories(query)
            .items
            .map { it.toDomain() }
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
        repo: String
    ): List<GitHubIssue> {
        return apiService.getIssues(owner, repo)
            .filter { it.pullRequest == null }
            .map { it.toDomain() }
    }

    override suspend fun getPullRequests(
        owner: String,
        repo: String
    ): List<GitHubPullRequest> {
        return apiService.getPullRequests(owner, repo)
            .map { it.toDomain() }
    }

    override suspend fun getCommits(
        owner: String,
        repo: String
    ): List<GitHubCommit> {
        return apiService.getCommits(owner, repo)
            .map { it.toDomain() }
    }
}