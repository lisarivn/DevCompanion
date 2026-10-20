package com.example.devcompanion.domain.model

data class PageResult<T>(
    val items: List<T>,
    val nextPage: Int?
)