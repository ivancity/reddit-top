package com.ivan.m.reddittimeline.dependency

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.ivan.m.reddittimeline.repo.MainRepository
import com.ivan.m.reddittimeline.services.ApiService
import com.ivan.m.reddittimeline.services.NetworkConstants.BASE_URL
import com.ivan.m.reddittimeline.services.NetworkConstants.SECURED_BASE_URL
import com.ivan.m.reddittimeline.ui.factory.ViewModelFactory
import com.ivan.m.reddittimeline.ui.home.dataStore

object Injection {
    private fun provideMainRepository(context: Context): MainRepository {
        return MainRepository(
            ApiService.create(BASE_URL),
            ApiService.create(SECURED_BASE_URL),
            context.dataStore
        )
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideMainRepository(context))
    }
}