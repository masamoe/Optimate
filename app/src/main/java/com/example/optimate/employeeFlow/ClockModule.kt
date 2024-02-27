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
import android.widget.ImageView
import com.example.optimate.loginAndRegister.DynamicLandingActivity
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ClockModule : AppCompatActivity() {
    private lateinit var digitalClock: TextView
    private lateinit var clockInButton: Button
    private lateinit var clockOutButton: Button
    private var isInClockInState = true
    private var isOnBreak = false // Added state to track break status
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedPreferences: SharedPreferences
    private val db = Firebase.firestore
    private lateinit var uid: String // Assuming this is set after user logs in


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_clock_module)

        digitalClock = findViewById(R.id.digitalClock)
        clockInButton = findViewById(R.id.clockIn)
        clockOutButton = findViewById(R.id.clockOut)
        sharedPreferences = getSharedPreferences("ClockingState", Context.MODE_PRIVATE)

        uid = GlobalUserData.uid // Set the current user's ID

        // Load the clocking and break states for the current user
        isInClockInState = sharedPreferences.getBoolean("${uid}_isInClockInState", false)
        isOnBreak = sharedPreferences.getBoolean("${uid}_isOnBreak", false)

        checkForExistingClockIn()
        // Update UI based on loaded states
        updateButtonStates()


        val homeBtn = findViewById<ImageView>(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, DynamicLandingActivity::class.java)
            startActivity(intent)
        }

        val viewHistoryBtn = findViewById<Button>(R.id.viewHistory)
        viewHistoryBtn.setOnClickListener {
            val intent = Intent(this, ViewHistory::class.java)
            startActivity(intent)
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
                saveWorkLogToDB("clockIn"){}
            }
            clickedButton == clockInButton && clockInButton.text == getString(R.string.clock_out) -> {
                // Clock Out state
                isInClockInState = false
                isOnBreak = false // Ensure not on break when clocking out
                saveWorkLogToDB("clockOut"){
                    calculateTotalHoursWorked()
                }

            }
            clickedButton == clockInButton && clockInButton.text == getString(R.string.start_break) -> {
                // Start Break state
                isOnBreak = true
                saveWorkLogToDB("breakStart"){}


            }
            clickedButton == clockInButton && clockInButton.text == getString(R.string.end_break) -> {
                // End Break state
                isOnBreak = false
                saveWorkLogToDB("breakEnd"){}
            }
            clickedButton == clockOutButton && clockOutButton.text == getString(R.string.start_break) -> {
                // Start Break state for Clock Out button
                isOnBreak = true
                saveWorkLogToDB("breakStart"){}
            }
        }

        // Save the clocking and break states
        sharedPreferences.edit().apply {
            putBoolean("${uid}_isInClockInState", isInClockInState)
            putBoolean("${uid}_isOnBreak", isOnBreak)
            apply()
        }

        // After toggling, update the button states
        updateButtonStates()
    }

    private fun saveWorkLogToDB(type: String, onComplete: () -> Unit) {
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
                    // No documents for today, create a new work log
                    val workLog = hashMapOf(
                        "uid" to uid,
                        "bid" to bid,
                        currentDate to listOf(entry)
                    )
                    db.collection("workLogs")
                        .add(workLog)
                        .addOnSuccessListener { documentReference ->
                            Log.d("WorkLog", "DocumentSnapshot added with ID: ${documentReference.id}")
                            checkForExistingClockIn()
                            onComplete()
                        }
                        .addOnFailureListener { e ->
                            Log.w("WorkLog", "Error adding document", e)
                        }
                } else {
                    // Update existing document with the new entry
                    val documentSnapshot = documents.documents[0]
                    val existingData = documentSnapshot.get(currentDate) as? List<*>

                    val updatedData = existingData?.toMutableList() ?: mutableListOf()
                    updatedData.add(entry)

                    documentSnapshot.reference.update(mapOf(
                        currentDate to updatedData
                    )).addOnCompleteListener {
                        checkForExistingClockIn()
                        onComplete()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("WorkLog", "Error getting documents.", e)
            }
    }

    // a function to check if the user has already clocked out, dont allow them to clock in again for the day
    // set the clockin button to grey and disable it
    private fun checkForExistingClockIn() {
        val bid = GlobalUserData.bid
        val uid = GlobalUserData.uid
        val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        db.collection("workLogs")
            .whereEqualTo("uid", uid)
            .whereEqualTo("bid", bid)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No documents for today, enable clock in
                    clockInButton.isEnabled = true
                } else {
                    //check if the user has already clocked out for current date,check current date list that has the clockout key
                    val documentSnapshot = documents.documents[0]
                    val existingData = documentSnapshot.get(currentDate) as? List<*>
                    if (existingData != null) {
                        for (entry in existingData) {
                            if (entry is Map<*, *>) {
                                if (entry.containsKey("clockOut")) {
                                    clockInButton.isEnabled = false
                                    clockInButton.backgroundTintList = getColorStateList(R.color.light_grey)
                                    clockInButton.setTextColor(getColor(R.color.grey))
                                    clockInButton.text = getString(R.string.already_clocked)
                                    return@addOnSuccessListener
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("WorkLog", "Error getting documents.", e)
            }

    }
    private fun saveTotalHoursToDB(date:String, totalHours: Long){
        val bid = GlobalUserData.bid
        val uid =GlobalUserData.uid

        val entry = hashMapOf(
            date to totalHours
        )
        db.collection("totalHours")
            .whereEqualTo("bid", bid)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No documents for today, create a new work log
                    val hours = hashMapOf(
                        "bid" to bid,
                        uid to listOf(entry)
                    )
                    db.collection("totalHours")
                        .add(hours)
                        .addOnSuccessListener { documentReference ->
                            Log.d("hours", "DocumentSnapshot added with ID: ${documentReference.id}")
                            checkForExistingClockIn()
                        }
                        .addOnFailureListener { e ->
                            Log.w("hours", "Error adding document", e)
                        }
                } else {
                    // Update existing document with the new entry
                    val documentSnapshot = documents.documents[0]
                    val existingData = documentSnapshot.get(uid) as? List<*>

                    val updatedData = existingData?.toMutableList() ?: mutableListOf()
                    updatedData.add(entry)

                    documentSnapshot.reference.update(mapOf(
                        uid to updatedData
                    )).addOnCompleteListener {
                        checkForExistingClockIn()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("hours", "Error getting documents.", e)
            }
    }

    // a function to calculate the total hours worked for the day
    //(clockIn - clockOut) - (breakStart + breakEnd)
    private fun calculateTotalHoursWorked() {
        val bid = GlobalUserData.bid
        val uid = GlobalUserData.uid
        val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())

        db.collection("workLogs")
            .whereEqualTo("uid", uid)
            .whereEqualTo("bid", bid)
            .get()
            .addOnSuccessListener { documents ->
                var totalDurationInMillis = 0L
                val clockInTimes = mutableListOf<Long>()
                val clockOutTimes = mutableListOf<Long>()
                val breakStartTimes = mutableListOf<Long>()
                val breakEndTimes = mutableListOf<Long>()

                documents.forEach { document ->
                    val existingData = document.get(currentDate) as? List<*>
                    existingData?.forEach { entry ->
                        if (entry is Map<*, *>) {
                            entry["clockIn"]?.let { clockInString ->
                                clockInTimes.add(dateFormat.parse(clockInString as String)?.time ?: 0)
                            }
                            entry["clockOut"]?.let { clockOutString ->
                                clockOutTimes.add(dateFormat.parse(clockOutString as String)?.time ?: 0)
                            }
                            entry["breakStart"]?.let { breakStartString ->
                                breakStartTimes.add(dateFormat.parse(breakStartString as String)?.time ?: 0)
                            }
                            entry["breakEnd"]?.let { breakEndString ->
                                breakEndTimes.add(dateFormat.parse(breakEndString as String)?.time ?: 0)
                            }
                        }
                    }
                }

                clockInTimes.forEachIndexed { index, clockInTime ->
                    if (index < clockOutTimes.size) {
                        var workDuration = clockOutTimes[index] - clockInTime

                        // Subtract any breaks that occurred during this period
                        breakStartTimes.forEachIndexed { breakIndex, breakStartTime ->
                            if (breakIndex < breakEndTimes.size && breakStartTime >= clockInTime && breakEndTimes[breakIndex] <= clockOutTimes[index]) {
                                workDuration -= (breakEndTimes[breakIndex] - breakStartTime)
                            }
                        }

                        totalDurationInMillis += workDuration
                    }
                }

                Log.d("calculateTotalHoursWorked", "Total Duration in Millis: $totalDurationInMillis")
                saveTotalHoursToDB(currentDate, totalDurationInMillis)
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


