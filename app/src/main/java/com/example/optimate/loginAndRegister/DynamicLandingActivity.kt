package com.example.optimate.loginAndRegister

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.example.optimate.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class DynamicLandingActivity : AppCompatActivity(){
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_landing)

        getAccountAccess(GlobalUserData.title, GlobalUserData.bid)





        val composeView= findViewById<ComposeView>(R.id.compose_view)

        composeView.setContent {
            DynamicLandingScreen()
        }
    }
    private fun getAccountAccess(title: String, bid: String){
        db.collection("titles")
            .whereEqualTo("title", title)
            .whereEqualTo("bid", bid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    GlobalUserData.access = document.data["access"] as List<String>
                    Log.d("Login", "access: ${GlobalUserData.access}")
                    Log.d("Login", "first: ${GlobalUserData.first_time}")

                }
            }
    }
}

