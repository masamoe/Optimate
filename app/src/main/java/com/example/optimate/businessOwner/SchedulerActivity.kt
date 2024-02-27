package com.example.optimate.businessOwner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.example.optimate.employeeFlow.RequestTimeOff
import com.example.optimate.employeeFlow.ViewTimeOffRequests
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SchedulerActivity : AppCompatActivity() {

    class ShiftInfo(val shiftDate: String,
                    val startTime: String?,
                    val endTime: String?,
                    val isScheduled: Boolean)
    private lateinit var selectedDateTextView: TextView
    private lateinit var nextShiftOrNotScheduledTextView: TextView
    private lateinit var scheduledOrNotTextView: TextView
    private lateinit var calendarView: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)

        selectedDateTextView = findViewById(R.id.selectedDate)
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

        val scheduleDateBtn = findViewById<Button>(R.id.scheduleDate)
        val viewTimeRequestsBtn = findViewById<Button>(R.id.viewTimeRequests)
        val requestTimeOffBtn = findViewById<Button>(R.id.requestTimeOff)

        scheduleDateBtn.setOnClickListener {
            startActivity(Intent(this, ScheduleMakerActivity::class.java))
        }

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
        // Example list of ShiftInfo
        val shiftList = listOf(
            ShiftInfo("2024-02-20", "09:00 AM", "05:00 PM", true), // Changed to "2024-02-20"
            ShiftInfo("2024-02-08", "10:00 AM", "06:00 PM", false)
        )

        val selectedDate = getDateFormattedFromMillis(dateInMillis)
        println("Selected Date: $selectedDate") // Debug log

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDateFormatted = dateFormat.format(dateInMillis)

        val scheduledShift = shiftList.find { it.shiftDate == selectedDateFormatted }
    }



    private fun getDateFormattedFromMillis(dateInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}