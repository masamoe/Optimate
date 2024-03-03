package com.example.optimate.loginAndRegister

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone



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
    var deviceToken: String = ""
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

fun getWage(uid: String, callback: (Double) -> Unit) {
    val db = Firebase.firestore

    db.collection("users")
        .whereEqualTo("UID", uid)
        .get()
        .addOnSuccessListener { querySnapshot ->
            querySnapshot.documents.forEach { documentSnapshot ->
                val user = documentSnapshot.data
                if (user != null) {
                    val wage = user["wage"] as Double
                    callback(wage)
                }
            }
        }
        .addOnFailureListener { e ->
            Log.w("ExpensesRequestsScreen", "Error getting user name", e)
        }
}

fun addRevenueOrExpenseToDB(type: String, dateStr: String, amount: Double, description: String) {
    val db = Firebase.firestore
    val bid = GlobalUserData.bid

    // Convert the String date to a Date object
    val sdf = SimpleDateFormat("MM/dd/yy", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }
    val date = sdf.parse(dateStr)

    val entry = hashMapOf(
        "UID" to (GlobalUserData.uid ?: ""),
        "Date" to date,
        "Amount" to amount,
        "Description" to description,
        "Approval" to true,
        "Name" to GlobalUserData.name,
        "Uploaded Date" to Timestamp(Date())
    )

    // Reference to the document in the finances collection
    val docRef = db.collection("finances").document(bid)

    // Check if the BID exists in the finances collection
    docRef.get().addOnSuccessListener { document ->
        if (document.exists()) {
            // If the document exists, append the new entry to the appropriate array
            val fieldName = if (type == "Revenue") "Revenues" else "Expenses"
            docRef.update(fieldName, com.google.firebase.firestore.FieldValue.arrayUnion(entry))
                .addOnSuccessListener {
                    Log.d("AddRevenueOrExpenseActivity", "$type added successfully")
                }
                .addOnFailureListener { e ->
                    Log.w("AddRevenueOrExpenseActivity", "Error adding $type", e)
                }
        } else {
            // If the document does not exist, create a new document with the BID and initialize the Revenues and Expenses arrays
            val initData = hashMapOf(
                "BID" to bid,
                "Revenues" to if (type == "Revenue") listOf(entry) else emptyList<Any>(),
                "Expenses" to if (type == "Expense") listOf(entry) else emptyList<Any>()
            )
            docRef.set(initData)
                .addOnSuccessListener {
                    Log.d("AddRevenueOrExpenseActivity", "New finances document with $type added successfully")
                }
                .addOnFailureListener { e ->
                    Log.w("AddRevenueOrExpenseActivity", "Error creating new finances document", e)
                }
        }
    }.addOnFailureListener { e ->
        Log.w("AddRevenueOrExpenseActivity", "Error checking document existence", e)
    }
}