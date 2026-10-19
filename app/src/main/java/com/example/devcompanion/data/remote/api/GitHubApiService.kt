package com.example.devcompanion.data.remote.api

import com.example.devcompanion.data.remote.dto.GitHubRepositoryDetailsDto
import com.example.devcompanion.data.remote.dto.RepositorySearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

import com.example.devcompanion.data.remote.dto.GitHubIssueDto
import com.example.devcompanion.data.remote.dto.GitHubPullRequestDto
import com.example.devcompanion.data.remote.dto.GitHubCommitDto

interface GitHubApiService {

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): RepositorySearchResponseDto

    @GET("repos/{owner}/{repo}")
    suspend fun getRepositoryDetails(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): GitHubRepositoryDetailsDto

    @GET("repos/{owner}/{repo}/issues")
    suspend fun getIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String = "all",
        @Query("per_page") perPage: Int = 30
    ): List<GitHubIssueDto>

    @GET("repos/{owner}/{repo}/pulls")
    suspend fun getPullRequests(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String = "all",
        @Query("per_page") perPage: Int = 30
    ): List<GitHubPullRequestDto>

    @GET("repos/{owner}/{repo}/commits")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("per_page") perPage: Int = 30
    ): List<GitHubCommitDto>
}