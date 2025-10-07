package com.example.links_app.models

data class ChatPreview(
    val chatId: String = "",
    val userId: String = "",
    val userName: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L
)
