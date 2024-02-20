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
import com.google.firebase.firestore.FirebaseFirestore


class ClockModule : AppCompatActivity() {
    private lateinit var digitalClock: TextView
    private lateinit var clockInButton: Button
    private lateinit var clockOutButton: Button
    private var isInClockInState = true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedPreferences: SharedPreferences
    // Initialize Firestore
    private val db = FirebaseFirestore.getInstance()
    private var currentWorkLog: WorkLog? = null

    // Function to save WorkLog object to Firestore
    private fun saveWorkLogToFirestore(workLog: WorkLog) {
        // Add the workLog object to Firestore
        db.collection("workLogs")
            .add(workLog)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
                currentWorkLog = workLog // Update currentWorkLog after saving to Firestore
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_module)
        digitalClock = findViewById(R.id.digitalClock)
        clockInButton = findViewById(R.id.clockIn)
        clockOutButton = findViewById(R.id.clockOut)
        sharedPreferences = getSharedPreferences("ClockingState", Context.MODE_PRIVATE)

        // Load the clocking state
        isInClockInState = sharedPreferences.getBoolean("isInClockInState", true)
        // Check if there's an existing work log
        // If yes, load it and update UI accordingly
        // Here, you need to implement a method to fetch the work log from Firestore based on your application logic
        // I'll assume there's a method called fetchWorkLogFromFirestore() for this purpose
        fetchWorkLogFromFirestore()

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

       /* viewHistoryButton.setOnClickListener {
            val intent = Intent(this, ViewHistory::class.java)
            startActivity(intent)
        }*/
    }

    /*private fun fetchWorkLogFromFirestore(userId: String, businessId: String) {
        // Construct the Firestore query to retrieve the work log document for the current user in the specified business
        db.collection("workLogs")
            .whereEqualTo("uid", userId) // Assuming 'uid' is the field that stores user ID
            .whereEqualTo("bid", businessId) // Assuming 'bid' is the field that stores business ID
            .get()
            .addOnSuccessListener { documents ->
                // Check if any document is returned
                if (!documents.isEmpty) {
                    // Get the first document (assuming there's only one work log per user in a business)
                    val documentSnapshot = documents.documents[0]
                    // Parse the data and update the currentWorkLog variable
                    currentWorkLog = documentSnapshot.toObject(WorkLog::class.java)
                    // You may also want to update the UI here based on the retrieved work log
                } else {
                    // No work log found for the user in the business, handle this case if needed
                }
            }
            .addOnFailureListener { e ->
                // Handle failure to fetch work log
                println("Error fetching work log: $e")
            }
    }*/

    private fun fetchWorkLogFromFirestore() {
        // Get the current user's ID, replace this with your actual method to get the user ID
        val userId = "asnckjsancjksankcas"
        // Get the current business's ID, replace this with your actual method to get the business ID
        val businessId = "business456"

        // Construct the Firestore query to retrieve the work log document for the current user and business
        db.collection("workLogs")
            .whereEqualTo("UID", userId)
            .whereEqualTo("BID", businessId)
            .get()
            .addOnSuccessListener { documents ->
                // Check if any document is returned
                if (!documents.isEmpty) {
                    // Get the first document (assuming there's only one work log per user for a business)
                    val documentSnapshot = documents.documents[0]
                    // Parse the data and update the currentWorkLog variable
                    currentWorkLog = documentSnapshot.toObject(WorkLog::class.java)
                    // You may also want to update the UI here based on the retrieved work log
                } else {
                    // No work log found for the user and business, handle this case if needed
                }
            }
            .addOnFailureListener { e ->
                // Handle failure to fetch work log
                println("Error fetching work log: $e")
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

                // Save the work log to Firestore
                currentWorkLog?.let { saveWorkLogToFirestore(it) }

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

                // Save the work log to Firestore
                currentWorkLog?.let { saveWorkLogToFirestore(it) }


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
