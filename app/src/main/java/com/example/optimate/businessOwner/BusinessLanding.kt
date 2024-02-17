package com.example.optimate.businessOwner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.example.optimate.loginAndRegister.GlobalUserData

class BusinessLanding : AppCompatActivity() {
    private lateinit var titlesBtn: ImageView
    private lateinit var accountsBtn: ImageView
    private lateinit var businessName: String
    private lateinit var username: TextView
    private lateinit var financesBtn: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_landing)
        businessName = GlobalUserData.name
        username = findViewById(R.id.username)
        username.text = businessName
        titlesBtn = findViewById(R.id.titles_btn)
        titlesBtn.setOnClickListener {
            // Go to the Titles activity
            startActivity(Intent(this, TitlesActivity::class.java))
        }
        accountsBtn = findViewById(R.id.accounts_btn)
        accountsBtn.setOnClickListener {
            // Go to the Accounts activity
            startActivity(Intent(this, AccountsActivity::class.java))
        }

        financesBtn = findViewById(R.id.finances_btn)
        financesBtn.setOnClickListener {
            // Go to the Finances activity
            startActivity(Intent(this, FinancesActivity::class.java))
        }
    }
}
