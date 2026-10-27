package com.example.devcompanion.data.mapper

import com.example.devcompanion.data.remote.dto.GitHubUserDto
import com.example.devcompanion.domain.model.GitHubUser

fun GitHubUserDto.toDomain(): GitHubUser {
    return GitHubUser(
        id = requireNotNull(id) { "GitHub user ID is missing" },
        login = login,
        avatarUrl = requireNotNull(avatarUrl) { "GitHub avatar URL is missing" },
        name = name,
        bio = bio,
        company = company,
        location = location,
        blog = blog,
        htmlUrl = requireNotNull(htmlUrl) { "GitHub profile URL is missing" },
        publicRepos = requireNotNull(publicRepos) { "Public repositories count is missing" },
        followers = requireNotNull(followers) { "Followers count is missing" },
        following = requireNotNull(following) { "Following count is missing" },
        createdAt = requireNotNull(createdAt) { "Account creation date is missing" }
    )
}