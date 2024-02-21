package com.example.optimate.employeeFlow

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Date

class RequestTimeOff : AppCompatActivity() {

    private var db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_time_off)
        var startTime = ""
        var endTime = ""
        var startDatetoDb: Date? = null
        var endDatetoDb: Date? = null




        // Create MaterialDatePicker instances for start and end dates
        val startDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select start date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        val endDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select end date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        // Find TextInputLayouts and TextInputEditTexts
        val outlinedStartDate = findViewById<TextInputLayout>(R.id.outlinedStartDate)
        val outlinedEndDate = findViewById<TextInputLayout>(R.id.outlinedEndDate)
        val startDateEditText = findViewById<TextView>(R.id.startDate)
        val endDateEditText = findViewById<TextView>(R.id.endDate)
        val sendButton = findViewById<Button>(R.id.sendButton)

        // Set click listeners to open date pickers
        outlinedStartDate.setEndIconOnClickListener {
            startDatePicker.show(supportFragmentManager, "START_DATE_PICKER_TAG")
        }
        outlinedEndDate.setEndIconOnClickListener {
            endDatePicker.show(supportFragmentManager, "END_DATE_PICKER_TAG")
        }

        startDateEditText.setOnClickListener {
                startDatePicker.show(supportFragmentManager, "START_DATE_PICKER_TAG")}

        endDateEditText.setOnClickListener {
                endDatePicker.show(supportFragmentManager, "END_DATE_PICKER_TAG")
            }


        // Set positive button click listeners to handle date selection
        startDatePicker.addOnPositiveButtonClickListener { startTimestamp ->
            val startDate = Date(startTimestamp)
            val endDate = endDatePicker.selection?.let { Date(it) }
            startDatetoDb = startDate

            if (endDate != null && startDate.after(endDate)) {
                outlinedStartDate.error = getString(R.string.start_date_after_end_date_error)
                startDateEditText.text = null
                startDatetoDb = null
            } else {
                outlinedStartDate.error = null
                startDateEditText.text = startDatePicker.headerText

            }
        }

        startDatePicker.addOnPositiveButtonClickListener { startTimestamp ->
            val startDate = Date(startTimestamp)
            val endDate = endDatePicker.selection?.let { Date(it) }
            startDatetoDb = startDate


            if (endDate != null && startDate.after(endDate)) {
                outlinedStartDate.error = getString(R.string.start_date_after_end_date_error)
                startDateEditText.text = null // Clear text when error occurs
                startDatetoDb = null
            } else {
                outlinedStartDate.error = null
                startDateEditText.text = startDatePicker.headerText
                outlinedEndDate.error = null // Clear error for end date when start date is selected
                startDatetoDb = null

            }
        }

        endDatePicker.addOnPositiveButtonClickListener { endTimestamp ->
            val endDate = Date(endTimestamp)
            val startDate = startDatePicker.selection?.let { Date(it) }

            endDatetoDb = endDate

            if (startDate != null && endDate.before(startDate)) {
                outlinedEndDate.error = getString(R.string.end_date_before_start_date_error)
                endDateEditText.text = null // Clear text when error occurs
                endDatetoDb = null
            } else {
                outlinedEndDate.error = null
                endDateEditText.text = endDatePicker.headerText
                endDatetoDb = null
            }
        }

// Create MaterialDatePicker instances for start and end dates
        val startTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(10)
            .setTitleText("Select Appointment time")
            .build()
        val endTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(10)
            .setTitleText("Select Appointment time")
            .build()

        // Find TextInputLayouts and TextInputEditTexts
        val startTimeEditText = findViewById<TextView>(R.id.startTime)
        val endTimeEditText = findViewById<TextView>(R.id.endTime)

        // Set OnClickListener for the Start Time EditText
        startTimeEditText.setOnClickListener{
            startTimePicker.show(supportFragmentManager, "START_Time_PICKER_TAG")
        }

// Set OnClickListener for the End Time EditText
        endTimeEditText.setOnClickListener{
            endTimePicker.show(supportFragmentManager, "END_Time_PICKER_TAG")
        }

// Add a listener to handle positive button click for Start Time
        startTimePicker.addOnPositiveButtonClickListener{
            val hour = if (startTimePicker.hour < 10) "0${startTimePicker.hour}" else startTimePicker.hour
            val minute = if (startTimePicker.minute < 10) "0${startTimePicker.minute}" else startTimePicker.minute
            val timeString = "$hour:$minute"
            startTimeEditText.text = timeString
            startTime =timeString
        }

// Add a listener to handle positive button click for End Time
        endTimePicker.addOnPositiveButtonClickListener{
            val hour = if (endTimePicker.hour < 10) "0${endTimePicker.hour}" else endTimePicker.hour
            val minute = if (endTimePicker.minute < 10) "0${endTimePicker.minute}" else endTimePicker.minute
            val timeString = "$hour:$minute"
            endTimeEditText.text = timeString
            endTime = timeString
        }

        sendButton.setOnClickListener{
            Toast.makeText(this, "button", Toast.LENGTH_SHORT).show()
            if (startDatetoDb != null && endDatetoDb != null) {
                Toast.makeText(this, "db", Toast.LENGTH_SHORT).show()
                saveTimeOffRequestToFirestore(startTime, endTime, startDatetoDb!!, endDatetoDb!!)

            }else {

                return@setOnClickListener
            }
        }


    }

    private fun saveTimeOffRequestToFirestore(startTime: String, endTime: String, startDate: Date, endDate: Date) {



        val timeOffRequest = hashMapOf(
            //"startTime" to startTime,
            //"endTime" to endTime,
            "uid" to "cankjlcnkjsanc",
            "bid" to "ncsakcnksajn",
            "name" to "sajknckjasn",
            "startDate" to startDate,
            "endDate" to endDate,
            "status" to "pending"
        )

        db.collection("timeOffRequest")
            .add(timeOffRequest)
            .addOnSuccessListener { documentReference ->
                Log.d("EditTimeOffRequest", "New record created with ID: ${documentReference.id}")

            }
            .addOnFailureListener { e ->
                Log.e("EditAccountActivity", "Error creating new record", e)
                // Handle the error, for example, show an error message to the user
            }
    }
}





