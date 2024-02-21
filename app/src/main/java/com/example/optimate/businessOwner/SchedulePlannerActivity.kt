package com.example.optimate.businessOwner

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SchedulePlannerActivity  : AppCompatActivity() {

    data class Amount(val type: String, val amount: String)
    data class AmountWithDate(val type: String, val amount: String, val date: String)

    private val db = Firebase.firestore
    private val bid = GlobalUserData.bid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Use remember with mutableStateOf to initialize the state
            val totalRevenues = remember { mutableDoubleStateOf(0.0) }
            val totalExpenses = remember { mutableDoubleStateOf(0.0) }
            val amountWithDate = remember { mutableStateListOf<AmountWithDate>() }

            // Update the UI with the latest values
            SchedulePlanner(

            )
        }
    }
}