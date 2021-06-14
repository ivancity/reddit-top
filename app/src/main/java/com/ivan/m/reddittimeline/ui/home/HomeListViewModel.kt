package com.ivan.m.reddittimeline.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ivan.m.reddittimeline.repo.MainRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class HomeListViewModel(private val repository: MainRepository) : ViewModel() {



    fun start() {
        viewModelScope.launch {
            try {
                val response = repository.getAccessToken()
                ensureActive()

                // write to DataStore
                val rest = repository.updateUserSettings(
                    response.accessToken,
                    response.expiresIn
                )

                Log.d("HomeViewModel", "start: TEST")

            }  catch (e: Exception) {
                // job was cancelled
                Log.e("HomeViewModel", "access token error")
            }
        }
    }



}

