package com.ivan.m.reddittimeline.repo

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ivan.m.reddittimeline.model.PostRemoteMediator
import com.ivan.m.reddittimeline.model.data.UserPreferences
import com.ivan.m.reddittimeline.model.data.db.PostDatabase
import com.ivan.m.reddittimeline.model.data.db.Posts
import com.ivan.m.reddittimeline.model.response.AccessTokenResponse
import com.ivan.m.reddittimeline.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import java.io.IOException

const val USER_PREFERENCES = "reddit_user_preferences"

class MainRepository(
    private var loginService: ApiService,
    private var service: ApiService,
    private val dataStore: DataStore<Preferences>,
    private val database: PostDatabase
) {

    private val TAG: String = "MainRepository"

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val accessToken = preferences[PreferencesKeys.ACCESS_TOKEN] ?: ""
            val expiresIn = preferences[PreferencesKeys.EXPIRES_IN] ?: 0
            UserPreferences(accessToken, expiresIn)
        }

    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val EXPIRES_IN = longPreferencesKey("expires_in")
    }

     suspend fun getAccessToken(): AccessTokenResponse {
         return withContext(Dispatchers.IO) {
             return@withContext loginService.getToken(
                 Credentials.basic("j29rupiuzagrHw", ""),
                 "https://oauth.reddit.com/grants/installed_client",
                 "DO_NOT_TRACK_THIS_DEVICE"
             )
         }
    }

    fun getAllPosts(): Flow<PagingData<Posts>> {
        val pagingSourceFactory = { database.postDao().allPosts() }
        val preferences = runBlocking { dataStore.data.first() }

        val accessToken = preferences[PreferencesKeys.ACCESS_TOKEN] ?: ""
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = PostRemoteMediator(
                service = service,
                database = database,
                token = accessToken
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }


    suspend fun updateUserSettings(accessToken: String, expiresIn: Long): UserPreferences {
        return withContext(Dispatchers.IO) {
            val preferences = dataStore.edit { preferences ->
                preferences[PreferencesKeys.ACCESS_TOKEN] = accessToken
                preferences[PreferencesKeys.EXPIRES_IN] = expiresIn
            }
            val token = preferences[PreferencesKeys.ACCESS_TOKEN] ?: ""
            val expires = preferences[PreferencesKeys.EXPIRES_IN] ?: 0
            UserPreferences(token, expires)
        }
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }
}