package com.example.devcompanion.data.remote.api

import com.example.devcompanion.data.remote.dto.GitHubDeviceCodeDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import com.example.devcompanion.data.remote.dto.GitHubAccessTokenDto

interface GitHubAuthApiService {

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("login/device/code")
    suspend fun requestDeviceCode(
        @Field("client_id") clientId: String,
        @Field("scope") scope: String = "read:user"
    ): GitHubDeviceCodeDto

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("login/oauth/access_token")
    suspend fun requestAccessToken(
        @Field("client_id") clientId: String,
        @Field("device_code") deviceCode: String,
        @Field("grant_type")
        grantType: String = "urn:ietf:params:oauth:grant-type:device_code"
    ): GitHubAccessTokenDto
}