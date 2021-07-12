package com.ivan.m.reddittimeline.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ivan.m.reddittimeline.repo.MainRepository
import com.ivan.m.reddittimeline.ui.home.HomeListViewModel

class ViewModelFactory(private val repository: MainRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}