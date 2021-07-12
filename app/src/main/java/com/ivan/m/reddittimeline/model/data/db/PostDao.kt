package com.ivan.m.reddittimeline.model.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<Posts>)

    @Query("SELECT * FROM posts")
    fun allPosts(): PagingSource<Int, Posts>

    @Query("DELETE FROM posts")
    suspend fun clearPosts()
}