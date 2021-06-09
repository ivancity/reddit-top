package com.ivan.m.reddittimeline.services

import com.ivan.m.reddittimeline.model.response.AccessTokenResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("api/v1/access_token")
    suspend fun getToken(
        @Header("Authorization") credentials: String,
        @Field("grant_type") grantType: String,
        @Field("device_id") deviceId: String
    ): AccessTokenResponse

    companion object {
        private const val BASE_URL = "https://www.reddit.com/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}