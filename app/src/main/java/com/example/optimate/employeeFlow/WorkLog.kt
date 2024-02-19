package com.example.optimate.employeeFlow

data class WorkLog(
    val uid: String,
    val bid: String,
    val name: String,
    val day: String,
    val clockIn: String = "",
    val clockOut: String = "",
    val breakStart: String = "",
    val breakEnd: String = ""
)

