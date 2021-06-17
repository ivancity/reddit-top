package com.ivan.m.reddittimeline.model.ui

enum class UiStatus {
    VALID, EMPTY, ERROR
}

data class HomeUi(
    val status: UiStatus,
    val items: List<ListItem>
)
