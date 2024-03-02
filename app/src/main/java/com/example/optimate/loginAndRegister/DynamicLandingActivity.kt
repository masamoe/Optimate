package com.example.optimate.loginAndRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import com.bumptech.glide.Glide
import com.example.optimate.R
import com.example.optimate.employeeFlow.EditProfile
import com.example.optimate.employeeFlow.ProfilePage
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class DynamicLandingActivity : AppCompatActivity(){
    val db = Firebase.firestore
    private lateinit var businessName: String
    private lateinit var username: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_landing)
        businessName = GlobalUserData.name
        username = findViewById(R.id.username)
        username.text = businessName
        val composeView = findViewById<ComposeView>(R.id.compose_view)
        val settingPage = findViewById<ImageView>(R.id.businessIcon)
        getAccountAccess(GlobalUserData.title, GlobalUserData.bid) {
            composeView.setContent {
                DynamicLandingScreen(GlobalUserData.access, GlobalUserData.title)
            }
        }
        if (GlobalUserData.profilePic != "") {
            Glide.with(this)
                .load(GlobalUserData.profilePic)
                .into(settingPage)
        }

        settingPage.setOnClickListener{
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Modify getAccountAccess to accept a callback function
    private fun getAccountAccess(title: String, bid: String, onAccessFetched: () -> Unit) {
        if (title == "businessOwner") {
            GlobalUserData.access = emptyList()
            Log.d("hihihi", "access: ${GlobalUserData.access}")
            Log.d("hihihi", "name: ${GlobalUserData.name}")
            onAccessFetched() // Invoke the callback immediately
        } else {
            db.collection("titles")
                .whereEqualTo("title", title)
                .whereEqualTo("bid", bid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        GlobalUserData.access = document.data["access"] as List<String>
                        Log.d("hihihi", "access: ${GlobalUserData.access}")
                        Log.d("hihihi", "name: ${GlobalUserData.name}")
                    }
                    onAccessFetched() // Invoke the callback after fetching the data
                }
        }
    }
}

