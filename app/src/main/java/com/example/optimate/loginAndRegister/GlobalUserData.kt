package com.example.optimate.loginAndRegister

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
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
    var phone: String = ""
    var first_time: Boolean = false
    var profilePic: String = ""
    lateinit var account_status: AccountStatus
    lateinit var modules: List<String>

}

fun uidToName(uid: String, callback: (String) -> Unit) {
    val db = Firebase.firestore

    db.collection("users")
        .whereEqualTo("UID", uid)
        .get()
        .addOnSuccessListener { querySnapshot ->
            querySnapshot.documents.forEach { documentSnapshot ->
                val user = documentSnapshot.data
                if (user != null) {
                    val name = user["name"] as String
                    callback(name)
                }
            }
        }
        .addOnFailureListener { e ->
            Log.w("payRequestsScreen", "Error getting user name", e)
            callback("") // Handle the failure case by providing an empty name
        }
}


fun milliSecondsToHours(milliSeconds: Long): Double {
    //in 2 decimal places
    return java.lang.String.format("%.2f", milliSeconds / 3600000.0).toDouble()
}