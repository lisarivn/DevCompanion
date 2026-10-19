package com.example.devcompanion.data.mapper

import com.example.devcompanion.data.remote.dto.GitHubCommitDto
import com.example.devcompanion.data.remote.dto.GitHubIssueDto
import com.example.devcompanion.data.remote.dto.GitHubPullRequestDto
import com.example.devcompanion.domain.model.GitHubCommit
import com.example.devcompanion.domain.model.GitHubIssue
import com.example.devcompanion.domain.model.GitHubPullRequest

fun GitHubIssueDto.toDomain() = GitHubIssue(
    id = id,
    number = number,
    title = title,
    state = state,
    author = user?.login ?: "Unknown",
    createdAt = createdAt,
    labels = labels.map { it.name }
)

fun GitHubPullRequestDto.toDomain() = GitHubPullRequest(
    id = id,
    number = number,
    title = title,
    state = state,
    author = user?.login ?: "Unknown",
    createdAt = createdAt,
    isMerged = mergedAt != null,
    headBranch = head.ref,
    baseBranch = base.ref
)

fun GitHubCommitDto.toDomain() = GitHubCommit(
    sha = sha,
    message = commit.message,
    author = commit.author?.name ?: "Unknown",
    date = commit.author?.date.orEmpty()
)