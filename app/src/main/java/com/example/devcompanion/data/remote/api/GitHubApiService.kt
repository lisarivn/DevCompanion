package com.example.devcompanion.data.remote.api

import com.example.devcompanion.data.remote.dto.RepositorySearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiService {

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): RepositorySearchResponseDto
}