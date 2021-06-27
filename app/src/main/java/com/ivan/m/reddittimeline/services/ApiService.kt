package com.ivan.m.reddittimeline.services

import com.ivan.m.reddittimeline.model.response.AccessTokenResponse
import com.ivan.m.reddittimeline.model.response.TopPostResponse
import com.ivan.m.reddittimeline.services.NetworkConstants.GET_POSTS_URL
import com.ivan.m.reddittimeline.services.NetworkConstants.LOGIN_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST(LOGIN_URL)
    suspend fun getToken(
        @Header("Authorization") credentials: String,
        @Field("grant_type") grantType: String,
        @Field("device_id") deviceId: String
    ): AccessTokenResponse


    @GET(GET_POSTS_URL)
    suspend fun fetchPosts(@Header("Authorization") token: String,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        @Query("count") count: Int? = null
    ): TopPostResponse

    companion object {
        fun create(baseUrl: String): ApiService {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }

    }
}