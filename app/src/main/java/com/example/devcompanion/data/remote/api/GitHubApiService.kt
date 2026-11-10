package com.example.devcompanion.data.remote.api

import com.example.devcompanion.data.remote.dto.GitHubCommitDto
import com.example.devcompanion.data.remote.dto.GitHubIssueDto
import com.example.devcompanion.data.remote.dto.GitHubPullRequestDto
import com.example.devcompanion.data.remote.dto.GitHubRepositoryDetailsDto
import com.example.devcompanion.data.remote.dto.RepositorySearchResponseDto
import com.example.devcompanion.data.remote.dto.GitHubUserDto
import com.example.devcompanion.data.remote.dto.GitHubEventDto
import com.example.devcompanion.data.remote.dto.GitHubRepositoryDto
import com.example.devcompanion.data.remote.dto.GitHubBranchResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Header

interface GitHubApiService {

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): Response<RepositorySearchResponseDto>

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
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): Response<List<GitHubIssueDto>>

    @GET("repos/{owner}/{repo}/issues/{issue_number}")
    suspend fun getIssueDetails(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNumber: Int
    ): GitHubIssueDto

    @GET("repos/{owner}/{repo}/pulls")
    suspend fun getPullRequests(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String = "all",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): Response<List<GitHubPullRequestDto>>

    @GET("repos/{owner}/{repo}/pulls/{pull_number}")
    suspend fun getPullRequestDetails(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullNumber: Int
    ): GitHubPullRequestDto

    @GET("repos/{owner}/{repo}/commits")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("sha") branch: String? = null,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): Response<List<GitHubCommitDto>>

    @GET("repos/{owner}/{repo}/commits/{ref}")
    suspend fun getCommitDetails(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("ref") sha: String
    ): GitHubCommitDto

    @GET("repos/{owner}/{repo}/branches")
    suspend fun getBranches(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1
    ): Response<List<GitHubBranchResponseDto>>

    @GET("users/{username}")
    suspend fun getUserProfile(
        @Path("username") username: String
    ): GitHubUserDto

    @GET("user")
    suspend fun getAuthenticatedUser(
        @Header("Authorization") authorization: String
    ): GitHubUserDto

    @GET("users/{username}/events/public")
    suspend fun getPublicEvents(
        @Path("username") username: String,
        @Query("per_page") perPage: Int = 10
    ): List<GitHubEventDto>

    @GET("user/repos")
    suspend fun getMyRepositories(
        @Header("Authorization") authorization: String,
        @Query("affiliation") affiliation: String = "owner",
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): Response<List<GitHubRepositoryDto>>

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String? = null,
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): Response<RepositorySearchResponseDto>

    @GET("user/repos")
    suspend fun getMyRepositories(
        @Header("Authorization") authorization: String,
        @Query("affiliation") affiliation: String = "owner",
        @Query("sort") sort: String = "updated",
        @Query("direction") direction: String = "desc",
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): Response<List<GitHubRepositoryDto>>
}