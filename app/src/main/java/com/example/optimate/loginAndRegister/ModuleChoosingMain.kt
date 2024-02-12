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
        var container1Picked = false
        var container2Picked = false
        var container3Picked = false
        val module1Price = 9.99
        val module2Price = 9.99
        val module3Price = 9.99
        var amount = findViewById<TextView>(R.id.Amount)
        val one = "one"
        val two = "two"
        val three = "three"
        


        container1.setOnClickListener {
            changeColor(container1)
            if (container1Picked) {
                amount.text = (amount.text.toString().toDouble() - module1Price).toString()
            } else {
                amount.text = (amount.text.toString().toDouble() + module1Price).toString()
            }
            container1Picked = !container1Picked
        }

        container2.setOnClickListener {
            changeColor(container2)
            if (container2Picked) {
                amount.text = (amount.text.toString().toDouble() - module2Price).toString()
            } else {
                amount.text = (amount.text.toString().toDouble() + module2Price).toString()
            }
            container2Picked = !container2Picked
        }

        container3.setOnClickListener {
            changeColor(container3)
            if (container3Picked) {
                amount.text = (amount.text.toString().toDouble() - module3Price).toString()
            } else {
                amount.text = (amount.text.toString().toDouble() + module3Price).toString()
            }
            container3Picked = !container3Picked
        }




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