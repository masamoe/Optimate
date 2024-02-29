package com.example.optimate.employeeFlow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class RequestTimeOff : AppCompatActivity() {

    private var db = Firebase.firestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_time_off)
        var startTime = ""
        var endTime = ""
        var startDatetoDb: Date? = null
        var endDatetoDb: Date? = null
        var reason = ""

        val allDaySwitch: MaterialSwitch = findViewById(R.id.materialSwitch)






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

            } else {
                outlinedStartDate.error = null
                startDateEditText.text = startDatePicker.headerText
                outlinedEndDate.error = null // Clear error for end date when start date is selected


            }
        }

        endDatePicker.addOnPositiveButtonClickListener { endTimestamp ->
            val endDate = Date(endTimestamp)
            val startDate = startDatePicker.selection?.let { Date(it) }

            endDatetoDb = endDate

            if (startDate != null && endDate.before(startDate)) {
                outlinedEndDate.error = getString(R.string.end_date_before_start_date_error)
                endDateEditText.text = null // Clear text when error occurs

            } else {
                outlinedEndDate.error = null
                endDateEditText.text = endDatePicker.headerText

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


        val textInputLayout: TextInputLayout = findViewById(R.id.textInputLayout)
        val autoCompleteTextView: AutoCompleteTextView = textInputLayout.editText as AutoCompleteTextView

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.reasons_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the AutoCompleteTextView
            autoCompleteTextView.setAdapter(adapter)
        }

        autoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            reason = parent.getItemAtPosition(position).toString()
            // Now the selected item is stored in the 'reason' variable
        }

        sendButton.setOnClickListener{

            if (allDaySwitch.isChecked) {
                 startTime = "12:00 AM"
                 endTime = "11:59 PM"
            }
            if (startDatetoDb != null && endDatetoDb != null) {


                saveTimeOffRequestToFirestore(startTime, endTime, startDatetoDb!!, endDatetoDb!!, reason )

            }else {

                return@setOnClickListener
            }
        }


    }

    private fun saveTimeOffRequestToFirestore(startTime: String, endTime: String, startDate: Date, endDate: Date, reason: String) {



        val timeOffRequest = hashMapOf(
            "dateOfRequest" to Date(),
            "startTime" to startTime,
            "endTime" to endTime,
            "uid" to GlobalUserData.uid,
            "bid" to GlobalUserData.bid,
            "name" to GlobalUserData.name,
            "startDate" to startDate,
            "endDate" to endDate,
            "status" to "pending",
            "Reason" to reason
        )

        db.collection("timeOffRequest")
            .add(timeOffRequest)
            .addOnSuccessListener { documentReference ->
                Log.d("EditTimeOffRequest", "New record created with ID: ${documentReference.id}")
                Toast.makeText(this, "Sent to Manager", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ScheduleModule::class.java))

            }
            .addOnFailureListener { e ->
                Log.e("EditAccountActivity", "Error creating new record", e)
                Toast.makeText(this, "Error in Sending", Toast.LENGTH_SHORT).show()
                // Handle the error, for example, show an error message to the user
            }
    }
}





