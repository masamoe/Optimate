package com.example.optimate.loginAndRegister

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.optimate.R

class ModuleChoosingMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module_choosing_main)
        val uid = intent.getStringExtra("USER_UID")
        val container1 = findViewById<TextView>(R.id.container1)
        val container2 = findViewById<TextView>(R.id.container2)
        val container3 = findViewById<TextView>(R.id.container3)

        container1.setOnClickListener { changeColor(container1) }
        container2.setOnClickListener { changeColor(container2) }
        container3.setOnClickListener { changeColor(container3) }
    }

    private fun changeColor(view: View) {
        val defaultColor = resources.getColor(R.color.grey)
        val clickedColor = resources.getColor(R.color.grey)

        if (view.background is ColorDrawable) {
            val currentColor = (view.background as ColorDrawable).color
            val newColor = if (currentColor == defaultColor) clickedColor else defaultColor
            view.setBackgroundColor(newColor)
        }
    }
}