package com.ivan.m.reddittimeline.dependency

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.ivan.m.reddittimeline.repo.MainRepository
import com.ivan.m.reddittimeline.services.ApiService
import com.ivan.m.reddittimeline.ui.factory.ViewModelFactory

object Injection {
    private fun provideMainRepository(context: Context): MainRepository {
        return MainRepository(ApiService.create())
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideMainRepository(context))
    }
}