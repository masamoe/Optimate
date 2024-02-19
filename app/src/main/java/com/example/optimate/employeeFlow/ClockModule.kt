package com.example.optimate.employeeFlow

import android.content.ContentValues.TAG

import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.optimate.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.*


class ClockModule : AppCompatActivity() {
    private lateinit var digitalClock: TextView
    private lateinit var clockInButton: Button
    private lateinit var clockOutButton: Button
    private var isInClockInState = true
    private var isInBreakState = false
    private val db = Firebase.firestore
    private val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    private val handler = Handler(Looper.getMainLooper())
    val uid = "asnckjsancjksankcas"
    val bid = "kasjnckjsanjcksan"
    private var startTime: Long = 0
    private var breakStart: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_module)
        digitalClock = findViewById(R.id.digitalClock)
        clockInButton = findViewById(R.id.clockIn)
        clockOutButton = findViewById(R.id.clockOut)

        // Initially disable the Clock Out button
        clockOutButton.isEnabled = false

        // Set click listener for the Clock In button
        clockInButton.setOnClickListener {
            toggleClockState(clockInButton)
        }

        // Set click listener for the Clock Out button
        clockOutButton.setOnClickListener {
            toggleClockState(clockOutButton)
        }

        // Initialize the handler for the digital clock updates
        handler.post(object : Runnable {
            override fun run() {
                updateDigitalClock()
                handler.postDelayed(this, 1000)
            }
        })


        val viewHistoryButton: Button = findViewById(R.id.viewHistory)

        viewHistoryButton.setOnClickListener {
            val intent = Intent(this, ViewHistory::class.java)
            startActivity(intent)
        }
    }

    private fun toggleClockState(clickedButton: Button) {
        val currentTime = System.currentTimeMillis()

        if (isInClockInState) {
            // Clock In or Start Break
            startTime = currentTime
            val formattedStartTime = dateFormat.format(Date(startTime))

            val workLogWithUid = WorkLog(
                uid = uid ?: "",
                bid = bid,
                name = "Your Name", // Provide the user's name here
                clockIn = formattedStartTime,

            )
            addWorkLogToFirestore(workLogWithUid)
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
        } else {
            // Clock Out or End Break
            val endTime = currentTime
            val formattedEndTime = dateFormat.format(Date(endTime))

            if (isInBreakState) {
                // End Break
                updateExistingWorkLog(startTime, endTime, formattedTime)
                Toast.makeText(this, formattedTime, Toast.LENGTH_SHORT).show()
            } else {
                // Clock Out
                updateExistingWorkLog(startTime, endTime, formattedTime)
                Toast.makeText(this, formattedTime, Toast.LENGTH_SHORT).show()
            }
        }
        isInClockInState = !isInClockInState
        isInBreakState = false
        // Update the UI based on the clock state

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

    private fun updateExistingWorkLog(startTime: Long, endTime: Long, formattedTime: String) {

        if (uid != null) {
            db.collection("workLogs")
                .whereEqualTo("uid", uid)
                .whereEqualTo("clockOut", null)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val workLog = document.toObject(WorkLog::class.java)
                        if (workLog != null) {
                            val updatedFields = hashMapOf<String, Any>(
                                "clockOut" to endTime,

                            )
                            if (isInBreakState) {
                                updatedFields["breakEnd"] = endTime

                            }
                            db.collection("workLogs")
                                .document(document.id)
                                .update(updatedFields)
                                .addOnSuccessListener {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error updating document", e)
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error getting documents", e)
                }
        }
    }

    private fun updateDigitalClock() {
        // Get the current time
        val currentTime = System.currentTimeMillis()

        // Format the time as HH:mm:ss
        val formattedTime = dateFormat.format(Date(currentTime))

        // Update the TextView with the formatted time
        digitalClock.text = formattedTime
    }
}
private fun addWorkLogToFirestore(workLog: WorkLog) {
    val db = Firebase.firestore
    db.collection("workLogs")
        .add(workLog)
        .addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Error adding document", e)
        }
}

data class WorkLog(
    val uid: String,
    val bid: String,
    val name: String,
    val clockIn: String = "",
    val clockOut: Long? = null,
    val breakStart: Long? = null,
    val breakEnd: Long? = null
)


