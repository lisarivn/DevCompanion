package com.example.devcompanion.domain.model

data class GitHubDeviceCode(
    val deviceCode: String,
    val userCode: String,
    val verificationUri: String,
    val expiresIn: Int,
    val interval: Int,
    val expiresAtElapsedRealtime: Long
)