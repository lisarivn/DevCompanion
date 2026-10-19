package com.example.devcompanion.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GitHubRepositoryDetailsDto(
    val id: Long,

    @SerializedName("full_name")
    val fullName: String,

    val name: String,
    val description: String?,
    val language: String?,

    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("stargazers_count")
    val starsCount: Int,

    @SerializedName("forks_count")
    val forksCount: Int,

    @SerializedName("open_issues_count")
    val openIssuesCount: Int,

    @SerializedName("default_branch")
    val defaultBranch: String,

    val owner: GitHubOwnerDto
)

data class GitHubOwnerDto(
    val login: String,

    @SerializedName("avatar_url")
    val avatarUrl: String
)