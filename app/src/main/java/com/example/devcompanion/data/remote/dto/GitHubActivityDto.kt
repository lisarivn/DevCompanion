package com.example.devcompanion.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GitHubUserDto(
    val login: String
)

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
    val pullRequest: GitHubPullRequestReferenceDto?
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
    val base: GitHubBranchDto
)

data class GitHubBranchDto(
    val ref: String
)

data class GitHubCommitDto(
    val sha: String,
    val commit: GitHubCommitInfoDto
)

data class GitHubCommitInfoDto(
    val message: String,
    val author: GitHubCommitAuthorDto?
)

data class GitHubCommitAuthorDto(
    val name: String?,
    val date: String?
)