package com.ivan.m.reddittimeline.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ivan.m.reddittimeline.repo.MainRepository
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ivan.m.reddittimeline.model.data.UserPreferences
import com.ivan.m.reddittimeline.model.ui.ListItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class HomeListViewModel(private val repository: MainRepository) : ViewModel() {

    private val userPreferences: Flow<UserPreferences> = repository.userPreferencesFlow
    private var currentResult: Flow<PagingData<ListItem>>? = null
    private val TAG: String = "HomeListViewModel"
    private var fetchTokenJob: Job? = null

    private fun start() {
        fetchTokenJob?.cancel()
        fetchTokenJob = viewModelScope.launch {
            try {
                val response = repository.getAccessToken()
                ensureActive()
                // write to DataStore
                repository.updateUserSettings(
                    response.accessToken,
                    response.expiresIn
                )
            }  catch (e: Exception) {
                Log.e(TAG, "access token error")
            }
        }
    }

    suspend fun initAccessToken() {
        try {
            val response = repository.getAccessToken()
            // write to DataStore
            repository.updateUserSettings(
                response.accessToken,
                response.expiresIn
            )
        }  catch (e: Exception) {
            Log.e(TAG, "access token error")
        }
    }

    fun getPosts(): Flow<PagingData<ListItem>> {
        val lastResult = currentResult
        if (lastResult != null) {
            return lastResult
        }

        val newResult: Flow<PagingData<ListItem>> = repository.getAllPosts()
            .map { pagingData -> pagingData.map {
                    ListItem(id = it.id,
                        author = it.author,
                        created = it.created,
                        title = it.title,
                        commentsCounter = it.commentsCounter,
                        thumbnail = it.thumbnail
                    )
                }
            }
            .cachedIn(viewModelScope)
        currentResult = newResult
        return newResult
    }
}

