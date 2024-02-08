package com.example.optimate.businessOwner

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.example.optimate.loginAndRegister.GlobalUserData

class BusinessLanding : AppCompatActivity(){
    private lateinit var titlesBtn: ImageView
    private lateinit var accountsBtn: ImageView
    private lateinit var businessName: String
    private lateinit var title: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_landing)
        businessName = GlobalUserData.name
        title = findViewById(R.id.titles)
        title.text = businessName
        titlesBtn = findViewById(R.id.titlesIcon)
        titlesBtn.setOnClickListener {
            // Go to the Titles activity
            startActivity(Intent(this, TitlesActivity::class.java))
        }
        accountsBtn = findViewById(R.id.accountsIcon)
        accountsBtn.setOnClickListener {
            // Go to the Accounts activity
            startActivity(Intent(this, AccountsActivity::class.java))
        }
    }
}