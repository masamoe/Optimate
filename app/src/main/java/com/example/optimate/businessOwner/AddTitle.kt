package com.example.optimate.businessOwner

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R

class AddTitle : AppCompatActivity(){
    private lateinit var homeBtn : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_title)

        homeBtn = findViewById(R.id.homeBtn)
        homeBtn.setOnClickListener {
            // Go to the BusinessLanding activity
            startActivity(Intent(this, BusinessLanding::class.java))
        }
    }
}