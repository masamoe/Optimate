package com.example.optimate.businessOwner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.example.optimate.loginAndRegister.GlobalUserData


class BusinessLanding : AppCompatActivity(){
    private lateinit var businessName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_landing)
        businessName = GlobalUserData.name

        val layout: GridLayout = findViewById<View>(R.id.choice_grid) as GridLayout
        val titleButton = LayoutButton(this, com.example.optimate.R.id.layoutBtn)
        titleButton.setID(R.id.layoutBtn,1)
        titleButton.setText(R.id.buttonTxt, "Change")
        titleButton.setImageResource(R.id.buttonImg, R.drawable.ic_roles_foreground)
        titleButton.setOnClickListener {
            startActivity(Intent(this, TitlesActivity::class.java))
        }

        layout.addView(titleButton)
// 'childView' can be any View object




    }
}