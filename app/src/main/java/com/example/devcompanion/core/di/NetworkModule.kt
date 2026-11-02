package com.example.devcompanion.core.di

import com.example.devcompanion.data.remote.api.GitHubApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.example.devcompanion.data.remote.api.GitHubAuthApiService

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGitHubApiService(
        retrofit: Retrofit
    ): GitHubApiService {
        return retrofit.create(GitHubApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGitHubAuthApiService(
        okHttpClient: OkHttpClient
    ): GitHubAuthApiService {
        return Retrofit.Builder()
            .baseUrl("https://github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubAuthApiService::class.java)
    }
}