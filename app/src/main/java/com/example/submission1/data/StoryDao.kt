package com.example.submission1.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(quote: List<StoriesResult>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, StoriesResult>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}