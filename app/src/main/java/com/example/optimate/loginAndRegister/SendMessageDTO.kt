package com.example.optimate.loginAndRegister

data class SendMessageDTO(
    val to: String?,
    val notification: NotificationBody
)

data class NotificationBody(
    val title: String,
    val body: String
)
