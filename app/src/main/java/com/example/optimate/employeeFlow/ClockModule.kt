package com.example.optimate.employeeFlow

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import com.example.optimate.R
import java.text.SimpleDateFormat
import java.util.*
import android.content.SharedPreferences



class ClockModule : AppCompatActivity() {
    private lateinit var digitalClock: TextView
    private lateinit var clockInButton: Button
    private lateinit var clockOutButton: Button
    private var isInClockInState = true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_module)
        digitalClock = findViewById(R.id.digitalClock)
        clockInButton = findViewById(R.id.clockIn)
        clockOutButton = findViewById(R.id.clockOut)
        sharedPreferences = getSharedPreferences("ClockingState", Context.MODE_PRIVATE)

        // Load the clocking state
        isInClockInState = sharedPreferences.getBoolean("isInClockInState", true)

        // Update UI based on loaded clocking state
        if (!isInClockInState) {
            // If not in clock in state, set clock out UI
            clockInButton.text = getString(R.string.clock_out)
            clockInButton.backgroundTintList = getColorStateList(R.color.light_red)
            clockOutButton.text = getString(R.string.start_break)
            clockOutButton.backgroundTintList = getColorStateList(R.color.light_yellow)
        }

        // Initialize the handler for the digital clock updates
        handler.post(object : Runnable {
            override fun run() {
                updateDigitalClock()
                handler.postDelayed(this, 1000)
            }
        })

        // Set click listeners for Clock In and Clock Out buttons
        clockInButton.setOnClickListener {
            toggleClockState(clockInButton)
        }

        clockOutButton.setOnClickListener {
            toggleClockState(clockOutButton)
        }

        val viewHistoryButton: Button = findViewById(R.id.viewHistory)

        viewHistoryButton.setOnClickListener {
            val intent = Intent(this, ViewHistory::class.java)
            startActivity(intent)
        }
    }
    private fun toggleClockState(clickedButton: Button) {
        // Toggle the clock state
        isInClockInState = !isInClockInState

        // Save the clocking state
        sharedPreferences.edit().putBoolean("isInClockInState", isInClockInState).apply()
        when {
            clickedButton == clockInButton && clockInButton.text == getString(R.string.clock_in) -> {
                // Clock In state
                clockInButton.text = getString(R.string.start_break)
                clockInButton.backgroundTintList = getColorStateList(R.color.light_yellow)
                clockOutButton.backgroundTintList = getColorStateList(R.color.light_red)
                clockOutButton.setTextColor(getColor(R.color.black))
                clockOutButton.isEnabled = true
            }
            clickedButton == clockInButton && clockInButton.text == getString(R.string.clock_out) -> {
                // Clock In state
                clockInButton.text = getString(R.string.clock_in)
                clockInButton.backgroundTintList = getColorStateList(R.color.light_green)
                clockOutButton.text = getString(R.string.clock_out)
                clockOutButton.isEnabled = false
                clockOutButton.backgroundTintList = getColorStateList(R.color.light_grey)
                clockOutButton.setTextColor(getColor(R.color.grey))

            }
            clickedButton == clockInButton && clockInButton.text == getString(R.string.start_break) -> {
                // Start Break state
                clockInButton.text = getString(R.string.end_break)
                clockInButton.backgroundTintList = getColorStateList(R.color.light_yellow)
            }
            clickedButton == clockInButton && clockInButton.text == getString(R.string.end_break) -> {
                // End Break state
                clockInButton.text = getString(R.string.clock_out)
                clockInButton.backgroundTintList = getColorStateList(R.color.light_red)
                clockOutButton.text = getString(R.string.start_break)
                clockOutButton.backgroundTintList = getColorStateList(R.color.light_yellow)

            }
            clickedButton == clockOutButton && clockOutButton.text == getString(R.string.start_break) -> {
                // Start Break state for Clock Out button
                clockInButton.text = getString(R.string.end_break)
                clockOutButton.text = getString(R.string.clock_out)
                clockInButton.backgroundTintList = getColorStateList(R.color.light_yellow)
                clockOutButton.backgroundTintList = getColorStateList(R.color.light_red)
            }
            clickedButton == clockOutButton && clockOutButton.text == getString(R.string.clock_out) -> {
                // Clock Out state
                clockOutButton.isEnabled = false
                clockInButton.text = getString(R.string.clock_in)
                clockInButton.backgroundTintList = getColorStateList(R.color.light_green)
                clockOutButton.text = getString(R.string.clock_out)
                clockOutButton.backgroundTintList = getColorStateList(R.color.light_grey)
                clockOutButton.setTextColor(getColor(R.color.grey))


            }
        }
    }



    private fun updateDigitalClock() {
        // Get the current time
        val currentTime = System.currentTimeMillis()

        // Format the time as HH:mm:ss
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = dateFormat.format(Date(currentTime))

        // Update the TextView with the formatted time
        digitalClock.text = formattedTime
    }

    override fun onDestroy() {
        // Remove the callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
