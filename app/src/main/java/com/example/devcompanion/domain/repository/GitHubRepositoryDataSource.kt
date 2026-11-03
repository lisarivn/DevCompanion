package com.example.devcompanion.domain.repository

import com.example.devcompanion.domain.model.GitHubCommit
import com.example.devcompanion.domain.model.GitHubIssue
import com.example.devcompanion.domain.model.GitHubPullRequest
import com.example.devcompanion.domain.model.GitHubRepository
import com.example.devcompanion.domain.model.GitHubRepositoryDetails
import com.example.devcompanion.domain.model.PageResult
import com.example.devcompanion.domain.model.GitHubUser
import com.example.devcompanion.domain.model.GitHubEvent

interface GitHubRepositoryDataSource {

    suspend fun searchRepositories(
        query: String,
        page: Int = 1
    ): PageResult<GitHubRepository>

    suspend fun getRepositoryDetails(
        owner: String,
        repo: String
    ): GitHubRepositoryDetails

    suspend fun getIssues(
        owner: String,
        repo: String,
        page: Int = 1
    ): PageResult<GitHubIssue>

    suspend fun getPullRequests(
        owner: String,
        repo: String,
        page: Int = 1
    ): PageResult<GitHubPullRequest>

    suspend fun getCommits(
        owner: String,
        repo: String,
        page: Int = 1
    ): PageResult<GitHubCommit>

    suspend fun getUserProfile(
        username: String): GitHubUser

    suspend fun getAuthenticatedUser(): GitHubUser

    suspend fun getPublicEvents(username: String): List<GitHubEvent>
}