package com.ivan.m.reddittimeline.model.ui

import com.ivan.m.reddittimeline.ui.home.ListItem

enum class UiStatus {
    VALID, EMPTY, ERROR
}

data class HomeUi(
    val status: UiStatus,
    val items: List<ListItem>
)
