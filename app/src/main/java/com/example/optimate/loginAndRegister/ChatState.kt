package com.example.optimate.loginAndRegister

data class ChatState(val isEnteringToken: Boolean = true,
                     val remoteToken: String = "",
                     val messageText: String = ""
)
