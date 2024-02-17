package com.example.optimate.loginAndRegister

import java.util.Date
data class AccountStatus(var date: Date, var status: String)
object GlobalUserData {
    var access: List<String> = emptyList()
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

fun signOut() {
    GlobalUserData.access = emptyList()
    GlobalUserData.uid = ""
    GlobalUserData.bid = ""
    GlobalUserData.address = ""
    GlobalUserData.name = ""
    GlobalUserData.email = ""
    GlobalUserData.title = ""
    GlobalUserData.role = ""
    GlobalUserData.wage = 0F
    GlobalUserData.password = ""
    GlobalUserData.first_time = false
    GlobalUserData.account_status = AccountStatus(Date(), "")
    GlobalUserData.modules = emptyList()
}