package com.ivan.m.reddittimeline.dependency

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.ivan.m.reddittimeline.repo.MainRepository
import com.ivan.m.reddittimeline.repo.USER_PREFERENCES
import com.ivan.m.reddittimeline.services.ApiService
import com.ivan.m.reddittimeline.ui.factory.ViewModelFactory
import com.ivan.m.reddittimeline.ui.home.dataStore

object Injection {
    private fun provideMainRepository(context: Context): MainRepository {
        return MainRepository(
            ApiService.create(),
            context.dataStore
        )
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideMainRepository(context))
    }
}