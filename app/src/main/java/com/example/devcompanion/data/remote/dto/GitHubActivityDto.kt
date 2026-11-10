package com.example.devcompanion.data.remote.dto

import com.google.gson.annotations.SerializedName


data class GitHubIssueDto(
    val id: Long,
    val number: Int,
    val title: String,
    val state: String,
    val user: GitHubUserDto?,

    @SerializedName("created_at")
    val createdAt: String,

    val labels: List<GitHubLabelDto>,

    @SerializedName("pull_request")
    val pullRequest: GitHubPullRequestReferenceDto?,

    val body: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null,

    @SerializedName("closed_at")
    val closedAt: String? = null,

    @SerializedName("html_url")
    val htmlUrl: String? = null
)


data class GitHubLabelDto(
    val name: String
)

class GitHubPullRequestReferenceDto


data class GitHubPullRequestDto(
    val id: Long,
    val number: Int,
    val title: String,
    val state: String,
    val user: GitHubUserDto?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("merged_at")
    val mergedAt: String?,

    val head: GitHubBranchDto,
    val base: GitHubBranchDto,

    val body: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null,

    @SerializedName("closed_at")
    val closedAt: String? = null,

    @SerializedName("html_url")
    val htmlUrl: String? = null
)

data class GitHubBranchDto(
    val ref: String
)


data class GitHubCommitStatsDto(
    val additions: Int,
    val deletions: Int,
    val total: Int
)

data class GitHubCommitFileDto(
    val filename: String,
    val status: String,
    val additions: Int,
    val deletions: Int,
    val changes: Int
)

data class GitHubCommitAuthorDto(
    val name: String?,
    val date: String?
)

data class GitHubCommitInfoDto(
    val message: String,
    val author: GitHubCommitAuthorDto?
)

data class GitHubCommitDto(
    val sha: String,
    val commit: GitHubCommitInfoDto,

    @SerializedName("html_url")
    val htmlUrl: String? = null,

    val stats: GitHubCommitStatsDto? = null,

    val files: List<GitHubCommitFileDto>? = null
)