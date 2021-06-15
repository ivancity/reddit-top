package com.ivan.m.reddittimeline.ui.home

data class ListItem(
    val id: String,
    val author: String,
    val created: Long,
    val title: String,
    val commentsCounter: Long,
    val thumbnail: String)
