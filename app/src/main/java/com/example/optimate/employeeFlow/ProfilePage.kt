package com.example.optimate.employeeFlow



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.optimate.R
import android.content.Intent



class ProfilePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)


        val viewAvailabilityBtn = findViewById<Button>(R.id.viewAvailabilityBtn)
        val editProfileBtn = findViewById<Button>(R.id.editProfile)

// Set OnClickListener for View Availability Button

        // Set OnClickListener for Edit Profile Button
        editProfileBtn.setOnClickListener {
            startActivity(Intent(this,EditProfile::class.java))
        }

        viewAvailabilityBtn.setOnClickListener {
            startActivity(Intent(this,Availability::class.java))
        }


    }
}