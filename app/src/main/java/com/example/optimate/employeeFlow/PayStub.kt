package com.example.optimate.employeeFlow

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import org.checkerframework.common.returnsreceiver.qual.This

class PayStub : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_stub)

        // Example call to the function with sample values
        showPayStub("2024-02-10", 3000.0, 5000.0)

        val viewMorePayStubsBtn = findViewById<Button>(R.id.viewMorePayStubsBtn)
        val submitExpensesBtn = findViewById<Button>(R.id.submitExpensesBtn)
        val viewExpensesBtn = findViewById<Button>(R.id.viewExpensesBtn)

        viewMorePayStubsBtn.setOnClickListener {
            startActivity(Intent(this,ViewAllPayStubs::class.java))
        }
        submitExpensesBtn.setOnClickListener {
            startActivity(Intent(this,SubmitExpenses::class.java))
        }
        viewExpensesBtn.setOnClickListener {
            startActivity(Intent(this,ViewExpenses::class.java))
        }
    }

    private fun showPayStub(date: String, netIncome: Double, maxIncome: Double) {
        // Find the CircularProgressIndicator
        val donutChart = findViewById<CircularProgressIndicator>(R.id.donutChart)

        // Calculate progress percentage
        val progressPercentage = (netIncome / maxIncome) * 100

        // Set indicator color
        donutChart.setIndicatorColor(getColor(R.color.light_green))

        // Set track color
        donutChart.trackColor = getColor(R.color.light_red)

        // Create ObjectAnimator for progress animation
        val progressAnimator = ObjectAnimator.ofInt(donutChart, "progress", 0, progressPercentage.toInt())
        progressAnimator.duration = 1000 // Duration of animation in milliseconds (adjust as needed)

        // Create AnimatorSet to play the progress animation
        val animatorSet = AnimatorSet()
        animatorSet.play(progressAnimator)
        animatorSet.start()
    }
}
