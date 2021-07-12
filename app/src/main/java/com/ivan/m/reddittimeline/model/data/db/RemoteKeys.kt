package com.ivan.m.reddittimeline.model.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val keyName: String,
    val prevKey: String?,
    val nextKey: String?,
    val counter: Int?,
)
