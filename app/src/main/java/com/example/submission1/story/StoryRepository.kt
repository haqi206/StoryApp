package com.example.submission1.story

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.submission1.data.*

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val pref: UserPreference
) {

    fun getStory(): LiveData<PagingData<StoriesResult>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, pref),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}