package com.example.submission1.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.submission1.data.ApiConfig
import com.example.submission1.data.StoryDatabase
import com.example.submission1.data.UserPreference
import com.example.submission1.story.StoryRepository

object Injection {
    fun provideRepository(context: Context, preference: DataStore<Preferences>): StoryRepository {

        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getDatabase(context)
        val pref = UserPreference.getInstance(preference)

        return StoryRepository(database, apiService, pref)
    }
}