package com.example.devcompanion.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RepositorySearchResponseDto(
    @SerializedName("total_count")
    val totalCount: Int,

    @SerializedName("items")
    val items: List<GitHubRepositoryDto>
)

data class GitHubRepositoryDto(
    val id: Long,

    @SerializedName("full_name")
    val fullName: String,

    val description: String?,
    val language: String?,

    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("stargazers_count")
    val starsCount: Int
)