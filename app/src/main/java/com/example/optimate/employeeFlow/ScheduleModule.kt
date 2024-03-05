package com.example.optimate.employeeFlow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.example.optimate.loginAndRegister.DynamicLandingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleModule : AppCompatActivity() {
    class ShiftInfo(
        val BID: String?,
        val day: String?,
        val employees: List<String>?,
        val endTime: String?,
        val startTime: String?
    )

    private lateinit var selectedDateTextView: TextView
    private lateinit var nextShiftOrNotScheduledTextView: TextView
    private lateinit var scheduledOrNotTextView: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_module)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val homeBtn = findViewById<ImageView>(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, DynamicLandingActivity::class.java)
            startActivity(intent)
        }

        selectedDateTextView = findViewById(R.id.selectedDate)
        nextShiftOrNotScheduledTextView = findViewById(R.id.nextShiftOrNotScheduled)
        scheduledOrNotTextView = findViewById(R.id.scheduledOrNot)
        calendarView = findViewById(R.id.calendarView)

        // Get today's date and format it
        val todayDate = getCurrentDateFormatted()

        // Set today's date initially
        selectedDateTextView.text = todayDate

        // Check for scheduled shifts
        checkScheduledShifts(calendarView.date)

        // Set an OnDateChangeListener to update the selected date and check for scheduled shifts
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = getDateFormatted(year, month, dayOfMonth)
            selectedDateTextView.text = selectedDate
            checkScheduledShifts(calendarView.date)
        }

        val viewTimeRequestsBtn = findViewById<Button>(R.id.viewTimeRequests)
        val requestTimeOffBtn = findViewById<Button>(R.id.requestTimeOff)

        viewTimeRequestsBtn.setOnClickListener {
            startActivity(Intent(this, ViewTimeOffRequests::class.java))
        }

        requestTimeOffBtn.setOnClickListener {
            startActivity(Intent(this, RequestTimeOff::class.java))
        }
    }

    private fun getCurrentDateFormatted(): String {
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    private fun getDateFormatted(year: Int, month: Int, dayOfMonth: Int): String {
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return dateFormat.format(calendar.time)
    }

    private fun checkScheduledShifts(dateInMillis: Long) {
        val user = auth.currentUser
        user?.let { currentUser ->
            // Fetch the shifts for the current user from the database
            val userName = currentUser.displayName

            if (userName != null) {
                val selectedDate = getDateFormattedFromMillis(dateInMillis)

                db.collection("schedule")
                    .whereArrayContains("employees", userName) // Adjust this based on your database structure
                    .whereEqualTo("day", selectedDate)
                    .get()
                    .addOnSuccessListener { documents ->
                        val shiftList = mutableListOf<ShiftInfo>()

                        for (document in documents) {
                            val shift = document.toObject(ShiftInfo::class.java)
                            shiftList.add(shift)
                            Log.d("FirestoreData", "Document ID: ${document.id}, Shift: $shift")
                        }

                        displayShiftData(shiftList)
                        val toastText = buildToastText(shiftList)
                        showToast(toastText)
                    }
                    .addOnFailureListener { exception ->
                        val failureToastText = "Failed to fetch shift data. Error: ${exception.message}"
                        showToast(failureToastText)
                    }
            }
        }
    }

    private fun displayShiftData(shiftList: List<ShiftInfo>) {
        // Clear previous text
        nextShiftOrNotScheduledTextView.text = ""
        scheduledOrNotTextView.text = ""

        if (shiftList.isNotEmpty()) {
            for (shiftInfo in shiftList) {
                val shiftDetails = "${shiftInfo.day} ${shiftInfo.startTime} to ${shiftInfo.endTime}"
                nextShiftOrNotScheduledTextView.append("You are scheduled next:\n")
                scheduledOrNotTextView.append("$shiftDetails\n")
            }
        } else {
            // No scheduled shift for the selected date or not scheduled
            nextShiftOrNotScheduledTextView.text = "Not scheduled yet!"
            scheduledOrNotTextView.text = ""
        }
    }

    private fun getDateFormattedFromMillis(dateInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun buildToastText(shiftList: List<ShiftInfo>): String {
        val toastTextBuilder = StringBuilder("Shifts:\n")
        for (shiftInfo in shiftList) {
            toastTextBuilder.append("${shiftInfo.day} ${shiftInfo.startTime} to ${shiftInfo.endTime}\n")
        }
        return toastTextBuilder.toString()
    }
}

