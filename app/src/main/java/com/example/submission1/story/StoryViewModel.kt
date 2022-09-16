package com.example.submission1.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submission1.data.StoriesResult
import com.example.submission1.data.User
import com.example.submission1.data.UserPreference
import kotlinx.coroutines.launch

class StoryViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository) : ViewModel() {

    val story: LiveData<PagingData<StoriesResult>> =
        storyRepository.getStory().cachedIn(viewModelScope)

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}