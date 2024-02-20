package com.example.optimate.employeeFlow

import java.util.*

object WorkLog {
    var uid: String = ""
    var bid: String = ""
    var name: String = ""
    var clockIn: String? = null
    var clockOut: String? = null
    var breaks: MutableList<Pair<String, String>> = mutableListOf()
}