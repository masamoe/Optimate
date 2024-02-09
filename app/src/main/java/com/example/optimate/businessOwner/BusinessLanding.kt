package com.example.optimate.businessOwner

import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.example.optimate.loginAndRegister.GlobalUserData

class BusinessLanding : AppCompatActivity() {
    private lateinit var businessName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_landing)
        businessName = GlobalUserData.name

        // Customize properties if needed
        // titleButton.setText(R.id.some_text_view_id, "Your Text")
        // titleButton.setImageResource(R.id.some_image_view_id, R.drawable.your_image)
    }
}
