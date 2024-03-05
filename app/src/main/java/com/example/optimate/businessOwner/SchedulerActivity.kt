package com.example.optimate.businessOwner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SchedulerActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private lateinit var dynamicContentContainer: LinearLayout

    data class Shift(
        val day: String,
        val employees: List<String>?,
        val startTime: String,
        val endTime: String
    ) {
        // Add a no-argument constructor
        constructor() : this(
            // Initialize your properties here if needed
            day = "",
            employees = null,
            startTime = "",
            endTime = ""
        )
    }

    class ShiftInfo(
        val shiftDate: String,
        val startTime: String?,
        val endTime: String?,
        val isScheduled: Boolean
    )

    private lateinit var selectedDateTextView: TextView
    private lateinit var nextShiftOrNotScheduledTextView: TextView
    private lateinit var scheduledOrNotTextView: TextView
    private lateinit var calendarView: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)

        val topBar: XmlTopBar = findViewById(R.id.topBar)
        topBar.setTitle("Schedule Overview")

        selectedDateTextView = findViewById(R.id.selectedDate)
        calendarView = findViewById(R.id.calendarView)
        dynamicContentContainer = findViewById(R.id.dynamicContentContainer)

        // Get today's date and format it
        val todayDate = getCurrentDateFormatted()

        // Set today's date initially
        selectedDateTextView.text = todayDate

        // Check for scheduled shifts
        checkScheduledShifts(calendarView.date)
        fetchAndPopulateShiftData(todayDate)

        // Set an OnDateChangeListener to update the selected date and check for scheduled shifts
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = getDateFormatted(year, month, dayOfMonth)
            selectedDateTextView.text = selectedDate

            // Clear existing views in dynamicContentContainer
            dynamicContentContainer.removeAllViews()

            // Fetch and populate new shift data for the selected date
            fetchAndPopulateShiftData(selectedDate)
        }

        val scheduleDateBtn = findViewById<Button>(R.id.scheduleDate)

        scheduleDateBtn.setOnClickListener {
            val selectedDate = selectedDateTextView.text.toString()

            val intent = Intent(this, AddShiftActivity::class.java)

            intent.putExtra("SELECTED_DATE", selectedDate)


            startActivity(intent)
        }
    }

    private fun fetchAndPopulateShiftData(selectedDate: String) {
        // Fetch shift data using the selected date
        getShiftData(selectedDate) { shifts ->
            populateUI(shifts)
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

    private fun populateUI(shifts: List<Shift>) {
        Log.d("SchedulerActivity", "Populating UI with ${shifts.size} shifts.")
        if (!::dynamicContentContainer.isInitialized) {
            dynamicContentContainer = findViewById(R.id.dynamicContentContainer)
        }

        // Create a map to group shifts by details
        val groupedShiftsMap = mutableMapOf<String, MutableList<String>>()

        // Populate the map with shifts and associated individuals
        for (shift in shifts) {
            val shiftDetails = "${shift.startTime} - ${shift.endTime}"
            val individuals = shift.employees ?: emptyList()

            if (groupedShiftsMap.containsKey(shiftDetails)) {
                groupedShiftsMap[shiftDetails]?.addAll(individuals)
            } else {
                groupedShiftsMap[shiftDetails] = individuals.toMutableList()
            }
        }

        // Sort the keys of groupedShiftsMap by start time
        val sortedShiftDetails = groupedShiftsMap.keys.sorted()

        // Iterate through the sorted keys
        for (shiftDetails in sortedShiftDetails) {
            val individuals = groupedShiftsMap[shiftDetails]

            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val contentView = inflater.inflate(R.layout.content_schedule_maker, null)
            dynamicContentContainer.addView(contentView)

            contentView.findViewById<TextView>(R.id.shiftHours).text = shiftDetails

            // Create a formatted string for the individuals working on the shift
            val names = StringBuilder()
            individuals?.forEach { name ->
                names.append(name).append(" \n")
            }

            // Remove the trailing newline if there are names
            if (names.isNotEmpty()) {
                names.setLength(names.length - 1)
            }

            contentView.findViewById<TextView>(R.id.NameFromDb).text = names.toString()
        }
    }


    private fun getDateFormattedFromMillis(dateInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getShiftData(selectedDate: String, callback: (List<Shift>) -> Unit) {
        db.collection("schedule")
            .whereEqualTo("BID", GlobalUserData.bid)
            .whereEqualTo("day", selectedDate)
            .get()
            .addOnSuccessListener { documents ->
                val shiftsList = mutableListOf<Shift>()
                for (document in documents) {
                    // Assuming Shift is a model class representing your shift data
                    val shift = document.toObject(Shift::class.java)
                    shiftsList.add(shift)
                }

                // Pass the list of Shift objects to the callback function
                callback(shiftsList)
            }
            .addOnFailureListener { exception ->
                Log.e("SchedulerActivity", "Error getting shift data", exception)
                // Handle failure if needed
            }
    }
}