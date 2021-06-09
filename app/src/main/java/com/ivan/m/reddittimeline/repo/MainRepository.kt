package com.ivan.m.reddittimeline.repo

import com.ivan.m.reddittimeline.model.response.AccessTokenResponse
import com.ivan.m.reddittimeline.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials

class MainRepository(
    private val service: ApiService
) {
     suspend fun getAccessToken(): AccessTokenResponse {
         return withContext(Dispatchers.IO) {
             return@withContext service.getToken(
                 Credentials.basic("j29rupiuzagrHw", ""),
                 "https://oauth.reddit.com/grants/installed_client",
                 "DO_NOT_TRACK_THIS_DEVICE"
             )
         }
    }
}