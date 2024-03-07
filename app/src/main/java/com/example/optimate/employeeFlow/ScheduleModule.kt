package com.example.optimate.employeeFlow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.optimate.R
import com.example.optimate.businessOwner.XmlTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ScheduleModule : AppCompatActivity() {
    class ShiftInfo(
        val BID: String?,
        val day: String?,
        val employees: List<String>?,
        val endTime: String?,
        val startTime: String?
    )

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
        val todayDate = getCurrentDateFormatted()

        // Check for scheduled shifts for the initial selected date
        checkScheduledShifts(materialCalendarView.firstSelectedDate.time)

        // Set up the listener for day clicks
        materialCalendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val selectedDate = getDateFormattedFromMillis(eventDay.calendar.timeInMillis)

                // Pass the time in milliseconds to checkScheduledShifts
                checkScheduledShifts(Date(eventDay.calendar.timeInMillis))
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
         showToast("Checked Schedule Shifts Month")

        user?.let { currentUser ->
            val userName = currentUser.displayName

            if (userName != null) {
                val calendar = Calendar.getInstance()
                calendar.set(year, month, 1)

                val firstDayOfMonth = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 1)
                val firstDayOfNextMonth = calendar.timeInMillis

                db.collection("schedule")
                    .whereEqualTo("BID" , GlobalUserData.bid)
                    .whereArrayContains("employees", userName)
                    .whereGreaterThanOrEqualTo("day", getDateFormattedFromMillis(firstDayOfMonth))
                    .whereLessThan("day", getDateFormattedFromMillis(firstDayOfNextMonth))
                    .get()
                    .addOnSuccessListener { documents ->
                        if (document.isEmpty) {
                            Log.e("FirestoreData","Schedule has no document")
                        }
                        showToast("Checked Schedule Shifts Month Success Getting")

                        val scheduledDates = mutableListOf<CalendarDay>()

                        for (document in documents) {
                            val shift = document.toObject(ShiftInfo::class.java)

                            // Check if shift.day is not null before attempting to convert
                            val shiftDateMillis =
                                shift.day?.let { getDateInMillisFromFormatted(it) } ?: 0L

                            // Create a new Calendar instance and set the time
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = shiftDateMillis
                            }

                            // Create a new CalendarDay instance and add it to scheduledDates
                            val calendarDay = CalendarDay(calendar)
                            scheduledDates.add(calendarDay)
                        }

                        val decorators = mutableListOf<EventDay>()

                        // Highlight the scheduled dates
                        for (calendarDay in scheduledDates) {
                            val decorator = EventDay(calendarDay.calendar, R.color.light_green)
                            decorators.add(decorator)
                        }

                        materialCalendarView.setEvents(decorators)
                    }
                    .addOnFailureListener { exception ->
                        val failureToastText =
                            "Failed to fetch scheduled dates. Error: ${exception.message}"
                        showToast(failureToastText)
                    }
            }
        }
    }

    private fun getCurrentDateFormatted(): String {
         showToast("Current Date Format")
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    private fun getDateFormatted(year: Int, month: Int, dayOfMonth: Int): String {
                 showToast("get Date Format")

        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return dateFormat.format(calendar.time)
    }

    private fun checkScheduledShifts(selectedDate: Date) {
        val user = auth.currentUser
        showToast("Checked Schedule Shifts Day")
        user?.let { currentUser ->
            // Fetch the shifts for the current user from the database
            val userName = currentUser.displayName

            if (userName != null) {
                val selectedDateString = getDateFormattedFromMillis(selectedDate.time)

                db.collection("schedule")
                    .whereEqualTo("BID", GlobalUserData.bid)
                    .whereArrayContains(
                        "employees",
                        userName
                    ) // Replace with the actual field name in your database
                    .whereEqualTo("day", selectedDateString)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (document.isEmpty) {
                            Log.e("FirestoreData","Scheduale has no document")
                        }
                         showToast("Checked Schedule Shifts Day Success Getting")
                        
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
                 showToast("Displaying shiftlist")

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
                 showToast("from millis Format")

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getDateInMillisFromFormatted(formattedDate: String): Long {
                 showToast("get millis Format")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(formattedDate)
        return date?.time ?: 0L
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

