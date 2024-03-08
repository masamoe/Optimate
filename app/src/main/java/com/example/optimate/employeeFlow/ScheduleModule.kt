package com.example.optimate.employeeFlow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.optimate.R
import com.example.optimate.businessOwner.XmlTopBar
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ScheduleModule : AppCompatActivity() {
    class ShiftInfo(
        val BID: String? = null,
        val day: String? = null,
        val employees: List<String>? = null,
        val endTime: String? = null,
        val startTime: String? = null
    ) {
        // Add a no-argument constructor
        constructor() : this(
            BID = null,
            day = null,
            employees = null,
            endTime = null,
            startTime = null
        )
    }

    private lateinit var nextShiftOrNotScheduledTextView: TextView
    private lateinit var scheduledOrNotTextView: TextView
    private lateinit var materialCalendarView: com.applandeo.materialcalendarview.CalendarView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_module)

        val topBar: XmlTopBar = findViewById(R.id.topBar)
        topBar.setTitle("Schedules")

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nextShiftOrNotScheduledTextView = findViewById(R.id.nextShiftOrNotScheduled)
        scheduledOrNotTextView = findViewById(R.id.scheduledOrNot)
        materialCalendarView = findViewById(R.id.calendarView)

        // Get today's date and format it
        var todayDate =
            getDateFormattedFromMillis(materialCalendarView.firstSelectedDate.timeInMillis)

        // Check for scheduled shifts for the initial selected date
        checkScheduledShifts(todayDate)

        // Set up the listener for day clicks
        materialCalendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val selectedDate = getDateFormattedFromMillis(eventDay.calendar.timeInMillis)

                // Pass the time in milliseconds to checkScheduledShifts
                checkScheduledShifts(selectedDate)
                todayDate = selectedDate
            }
        })

        val viewTimeRequestsBtn = findViewById<Button>(R.id.viewTimeRequests)
        val requestTimeOffBtn = findViewById<Button>(R.id.requestTimeOff)

        viewTimeRequestsBtn.setOnClickListener {
            startActivity(Intent(this, ViewTimeOffRequests::class.java))
        }

        requestTimeOffBtn.setOnClickListener {
            startActivity(Intent(this, RequestTimeOff::class.java))
        }

        // Fetch and highlight scheduled dates for the current month
        fetchScheduledDatesForMonth(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH)
        )
    }

    private fun fetchScheduledDatesForMonth(year: Int, month: Int) {
        val user = auth.currentUser
        user?.let { currentUser ->
            val userName = GlobalUserData.name
            if (userName != null) {
                val calendar = Calendar.getInstance()
                calendar.set(year, month, 1)
                val firstDayOfMonth = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 1)
                val firstDayOfNextMonth = calendar.timeInMillis

                db.collection("schedule")
                    .whereEqualTo("BID", GlobalUserData.bid)
                    .whereArrayContains("employees", userName)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            Log.e("FirestoreData", "No scheduled documents found")
                        } else {
                            val events = mutableListOf<EventDay>()

                            for (document in documents) {
                                val shift = document.toObject(ShiftInfo::class.java)

                                // Check if shift.day is not null before attempting to convert
                                val shiftDateMillis =
                                    shift.day?.let { getDateInMillisFromFormatted(it) } ?: 0L
                                // Perform the comparison locally
                                if (shiftDateMillis in firstDayOfMonth..<firstDayOfNextMonth) {
                                    val calendar = Calendar.getInstance().apply {
                                        timeInMillis = shiftDateMillis
                                    }
                                    val eventDay = EventDay(calendar, R.drawable.arrow_drop_up)
                                    events.add(eventDay)

                                    // Logging added
                                    Log.d("ScheduledDates", "Added event for: ${getDateFormattedFromMillis(shiftDateMillis)}")
                                }
                            }

                            materialCalendarView.setEvents(events)
                        }
                    }
                    .addOnFailureListener { exception ->
                        val failureToastText =
                            "Failed to fetch scheduled dates. Error: ${exception.message}"
                        showToast(failureToastText)
                        Log.e("FirestoreData", "Error fetching scheduled dates", exception)
                    }
            }
        }
    }

    private fun checkScheduledShifts(selectedDate: String) {
        val user = auth.currentUser
        user?.let { currentUser ->
            // Fetch the shifts for the current user from the database
            val userName = GlobalUserData.name

            if (userName != null) {
                val selectedDateString = selectedDate

                db.collection("schedule")
                    .whereEqualTo("BID", GlobalUserData.bid)
                    .whereArrayContains(
                        "employees",
                        userName
                    ) // Replace with the actual field name in your database
                    .whereEqualTo("day", selectedDateString)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            Log.e("FirestoreData", "checkScheduledShifts: Schedule has no document")
                        }

                        val shiftList = mutableListOf<ShiftInfo>()

                        for (document in documents) {
                            val shift = document.toObject(ShiftInfo::class.java)
                            shiftList.add(shift)
                            Log.d("FirestoreData", "Document ID: ${document.id}, Shift: $shift")
                        }

                        displayShiftData(shiftList)

                    }
                    .addOnFailureListener { exception ->
                        val failureToastText =
                            "Failed to fetch shift data. Error: ${exception.message}"
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
            nextShiftOrNotScheduledTextView.append("You are scheduled:")
            for (shiftInfo in shiftList) {
                val shiftDetails = "${shiftInfo.startTime} to ${shiftInfo.endTime}"
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
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getDateInMillisFromFormatted(formattedDate: String): Long {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val date = dateFormat.parse(formattedDate)
        return date?.time ?: 0L
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

