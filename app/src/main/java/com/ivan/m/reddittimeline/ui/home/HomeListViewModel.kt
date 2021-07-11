package com.ivan.m.reddittimeline.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ivan.m.reddittimeline.repo.MainRepository
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ivan.m.reddittimeline.model.data.UserPreferences
import com.ivan.m.reddittimeline.model.ui.HomeUi
import com.ivan.m.reddittimeline.model.ui.ListItem
import com.ivan.m.reddittimeline.model.ui.UiStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import retrofit2.HttpException

class HomeListViewModel(private val repository: MainRepository) : ViewModel() {

    private val userPreferences: Flow<UserPreferences> = repository.userPreferencesFlow

    private var currentResult: Flow<PagingData<ListItem>>? = null

    private val TAG: String = "HomeListViewModel"

    val homeUi: LiveData<HomeUi> = userPreferences
        .mapLatest { userPreferences ->
            if (userPreferences.accessToken.isEmpty()) {
                start()
                return@mapLatest HomeUi(status = UiStatus.EMPTY, emptyList())
            }

            val response = repository.getPosts(userPreferences.accessToken)
            val children = response.data?.children
            if (children.isNullOrEmpty()) {
                return@mapLatest HomeUi(status = UiStatus.EMPTY, emptyList())
            }

            val list: List<ListItem> = children.mapNotNull { child ->
                if (child.data.author == null
                    || child.data.created == null
                    || child.data.title == null
                    || child.data.numComments == null
                    || child.data.thumbnail == null) {
                    return@mapNotNull null
                }

                return@mapNotNull ListItem(
                    id = child.data.name,
                    author = child.data.author,
                    created = child.data.created,
                    title = child.data.title,
                    commentsCounter = child.data.numComments,
                    thumbnail = child.data.thumbnail
                )
            }

            return@mapLatest HomeUi(status = UiStatus.VALID, items = list)
        }
        .catch() { exception ->
            if (exception is HttpException) {
                if (exception.code() == 401) {
                    start()
                }
                emit(HomeUi(status = UiStatus.ERROR, items = emptyList()))
            } else {
                throw exception
            }
        }
        .asLiveData()

    private fun start() {
        viewModelScope.launch {
            try {
                val response = repository.getAccessToken()
                ensureActive()
                // write to DataStore
                val rest = repository.updateUserSettings(
                    response.accessToken,
                    response.expiresIn
                )
            }  catch (e: Exception) {
                Log.e(TAG, "access token error")
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
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

