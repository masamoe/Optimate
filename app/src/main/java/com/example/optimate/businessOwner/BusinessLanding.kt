package com.example.optimate.businessOwner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R

class BusinessLanding : AppCompatActivity(){
    private lateinit var titlesBtn: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_landing)
        titlesBtn = findViewById(R.id.titlesIcon)
        titlesBtn.setOnClickListener {
            // Go to the Titles activity
            startActivity(Intent(this, TitlesActivity::class.java))
        }
    }
}