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
import android.util.Log
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.time.format.DateTimeFormatter
class ClockModule : AppCompatActivity() {
    private lateinit var digitalClock: TextView
    private lateinit var clockInButton: Button
    private lateinit var clockOutButton: Button
    private var isInClockInState = true
    private var isOnBreak = false // Added state to track break status
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedPreferences: SharedPreferences
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_module)
        digitalClock = findViewById(R.id.digitalClock)
        clockInButton = findViewById(R.id.clockIn)
        clockOutButton = findViewById(R.id.clockOut)
        sharedPreferences = getSharedPreferences("ClockingState", Context.MODE_PRIVATE)

        // Load the clocking and break states
        isInClockInState = sharedPreferences.getBoolean("isInClockInState", true)
        isOnBreak = sharedPreferences.getBoolean("isOnBreak", false)

        // Update UI based on loaded states
        updateButtonStates()

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
    }

    private fun updateButtonStates() {
        if (isOnBreak) {
            // UI for break state
            clockInButton.text = getString(R.string.end_break)
            clockInButton.backgroundTintList = getColorStateList(R.color.light_yellow)
            clockOutButton.isEnabled = false
        } else if (!isInClockInState) {
            // UI for clocked out state
            clockInButton.text = getString(R.string.clock_in)
            clockInButton.backgroundTintList = getColorStateList(R.color.light_green)
            clockOutButton.text = getString(R.string.clock_out)
            clockOutButton.isEnabled = false
            clockOutButton.backgroundTintList = getColorStateList(R.color.light_grey)
            clockOutButton.setTextColor(getColor(R.color.grey))
        } else {
            // UI for clocked in state
            clockInButton.text = getString(R.string.clock_out)
            clockInButton.backgroundTintList = getColorStateList(R.color.light_red)
            clockOutButton.text = getString(R.string.start_break)
            clockOutButton.isEnabled = true
            clockOutButton.backgroundTintList = getColorStateList(R.color.light_yellow)
        }
    }

    private fun toggleClockState(clickedButton: Button) {
        when {
            clickedButton == clockInButton && clockInButton.text == getString(R.string.clock_in) -> {
                // Clock In state
                isInClockInState = true
                isOnBreak = false // Ensure not on break when clocking in
                saveWorkLogToDB("clockIn")
            }
            clickedButton == clockInButton && clockInButton.text == getString(R.string.clock_out) -> {
                // Clock Out state
                isInClockInState = false
                isOnBreak = false // Ensure not on break when clocking out
                saveWorkLogToDB("clockOut")
            }
            clickedButton == clockInButton && clockInButton.text == getString(R.string.start_break) -> {
                // Start Break state
                isOnBreak = true
                saveWorkLogToDB("breakStart")
            }
            clickedButton == clockInButton && clockInButton.text == getString(R.string.end_break) -> {
                // End Break state
                isOnBreak = false
                saveWorkLogToDB("breakEnd")
            }
            clickedButton == clockOutButton && clockOutButton.text == getString(R.string.start_break) -> {
                // Start Break state for Clock Out button
                isOnBreak = true
                saveWorkLogToDB("breakStart")
            }
        }

        // Save the clocking and break states
        sharedPreferences.edit().apply {
            putBoolean("isInClockInState", isInClockInState)
            putBoolean("isOnBreak", isOnBreak)
            apply()
        }

        // After toggling, update the button states
        updateButtonStates()
    }

    private fun saveWorkLogToDB(type: String) {
        val bid = GlobalUserData.bid
        val uid = GlobalUserData.uid
        // Format the current Date and Time into a readable string
        val dateTimeFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())
        val formattedDateTime = dateTimeFormat.format(Date())

        val entry = hashMapOf(
            type to formattedDateTime
        )

        // Get the current date
        val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        db.collection("workLogs")
            .whereEqualTo("uid", uid)
            .whereEqualTo("bid", bid)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val workLog = hashMapOf(
                        "uid" to uid,
                        "bid" to bid,
                        currentDate to listOf(entry)
                    )
                    db.collection("workLogs")
                        .add(workLog)
                        .addOnSuccessListener { documentReference ->
                            Log.d("WorkLog", "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w("WorkLog", "Error adding document", e)
                        }
                } else {
                    val documentSnapshot = documents.documents[0]
                    val existingData = documentSnapshot.get(currentDate)

                    if (existingData == null) {
                        // currentDate key does not exist, create a new list for currentDate
                        documentSnapshot.reference.update(currentDate, listOf(entry))
                    } else {
                        // currentDate key exists, append to its list
                        val clocks: MutableList<HashMap<String, String>> = if (existingData is List<*>) {
                            existingData.mapNotNull { it as? HashMap<String, String> }.toMutableList()
                        } else {
                            mutableListOf()
                        }

                        clocks.add(entry)
                        documentSnapshot.reference.update(currentDate, clocks)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("WorkLog", "Error getting documents.", e)
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


