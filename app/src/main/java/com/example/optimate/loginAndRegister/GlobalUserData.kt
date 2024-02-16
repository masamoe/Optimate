package com.example.optimate.loginAndRegister

import java.util.Date
data class AccountStatus(var date: Date, var status: String)
object GlobalUserData {
    var uid: String = ""
    var bid: String = ""
    var address: String = ""
    var name: String = ""
    var email: String = ""
    var title: String = ""
    var role: String = ""
    var wage: Float = 0F
    var password: String = ""
    var first_time: Boolean = false
    lateinit var account_status: AccountStatus
    lateinit var modules: List<String>

}