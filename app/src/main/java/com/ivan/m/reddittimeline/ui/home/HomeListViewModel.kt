package com.ivan.m.reddittimeline.ui.home

import androidx.lifecycle.ViewModel
import com.ivan.m.reddittimeline.repo.MainRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class HomeListViewModel(private val repository: MainRepository) : ViewModel() {

    fun start() {
        val deferredResponse = viewModelScope.launch {
            val token = repository.getAccessToken().accessToken

        }
    }

}