package com.example.devcompanion.core.di

import com.example.devcompanion.data.repository.GitHubRepositoryImpl
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GitHubRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGitHubRepository(
        implementation: GitHubRepositoryImpl
    ): GitHubRepositoryDataSource
}