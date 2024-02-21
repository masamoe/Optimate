package com.example.optimate.loginAndRegister
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import android.content.SharedPreferences

class LandingMain : AppCompatActivity(){
    private var startBtn: Button? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_main)


        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Check if the user has signed in before
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            // If the user has signed in before, direct them to the login page
            startActivity(Intent(this, Login::class.java))
            finish() // Finish this activity to prevent the user from coming back to it when pressing the back button
            return // Return to prevent executing further code in this method
        }

        startBtn = findViewById(R.id.startBtn)
        //when user clicks on start button, it will take them to the login page
        startBtn?.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }

}

