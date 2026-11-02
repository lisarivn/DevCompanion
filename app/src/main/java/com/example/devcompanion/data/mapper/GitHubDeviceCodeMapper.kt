package com.example.devcompanion.data.mapper

import com.example.devcompanion.data.remote.dto.GitHubDeviceCodeDto
import com.example.devcompanion.domain.model.GitHubDeviceCode

fun GitHubDeviceCodeDto.toDomain(): GitHubDeviceCode {
    return GitHubDeviceCode(
        deviceCode = deviceCode,
        userCode = userCode,
        verificationUri = verificationUri,
        expiresIn = expiresIn,
        interval = interval
    )
}