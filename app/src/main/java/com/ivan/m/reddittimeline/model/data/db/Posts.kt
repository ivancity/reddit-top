package com.ivan.m.reddittimeline.model.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Posts(
    @PrimaryKey val id: String,
    val author: String?,
    val created: Long?,
    val title: String?,
    val commentsCounter: Long?,
    val thumbnail: String?
)
