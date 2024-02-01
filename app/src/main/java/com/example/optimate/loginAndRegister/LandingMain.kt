package com.example.optimate.loginAndRegister
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R

class LandingMain : AppCompatActivity(){
    private var startBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_main)

        startBtn = findViewById(R.id.startBtn)
        //when user clicks on start button, it will take them to the login page
        startBtn?.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }

}

