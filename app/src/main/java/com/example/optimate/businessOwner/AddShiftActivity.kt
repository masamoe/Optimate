package com.example.optimate.businessOwner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateListOf
import com.example.optimate.R
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddShiftActivity : AppCompatActivity() {

    private var employeeNames = mutableStateListOf<String>()

    private var db = Firebase.firestore
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dateTextView: TextView
    private lateinit var startTimeEditText: EditText
    private lateinit var endTimeEditText: EditText

    data class Shift(
        val day: String,
        val employees: List<String>,
        val startTime: String,
        val endTime: String
    )

    private val selectedEmployees = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_shift)
        val saveShiftBtn = findViewById<Button>(R.id.saveShiftButton)

        val topBar: XmlTopBar = findViewById(R.id.topBar)
        topBar.setTitle("Add Shift")

        dateTextView = findViewById(R.id.dateTextView)
        val selectedDate = intent.getLongExtra("SELECTED_DATE", 0)

        // Convert the selected date to a formatted string
        val formattedDate = getDateFormattedFromMillis(selectedDate)
        // Set the text of editTextDate to the selected date
        dateTextView.text = formattedDate

        startTimeEditText = findViewById(R.id.startTime)
        endTimeEditText = findViewById(R.id.endTime)
        val employeeListView: ListView = findViewById(R.id.employeeListView)

        dateTextView.setOnClickListener {
            showDatePicker()
        }

        startTimeEditText.setOnClickListener {
            showTimePicker(startTimeEditText)
        }

        endTimeEditText.setOnClickListener {
            showTimePicker(endTimeEditText)
        }

        adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_multiple_choice,
            android.R.id.text1,
            employeeNames
        )

        employeeListView.adapter = adapter
        employeeListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        // Set up listener for item selection/deselection
        employeeListView.setOnItemClickListener { _, _, position, _ ->
            // Handle item selection/deselection
            val employeeName = employeeNames[position]
            if (selectedEmployees.contains(employeeName)) {
                selectedEmployees.remove(employeeName)
            } else {
                selectedEmployees.add(employeeName)
            }
            // Perform any additional actions based on selection/deselection
        }

        saveShiftBtn.setOnClickListener {
            saveShiftToFirebase(formattedDate, startTimeEditText, endTimeEditText)
        }

        // Now, call getEmployeesFromDB() after adapter initialization
        getEmployeesFromDB()
    }

    private fun saveShiftToFirebase(
        selectedDate: String,
        startTimeEditText: EditText,
        endTimeEditText: EditText
    ) {
        val startTime = startTimeEditText.text.toString()
        val endTime = endTimeEditText.text.toString()

        val shift = Shift(
            day = selectedDate,
            employees = selectedEmployees,
            startTime = startTime,
            endTime = endTime
        )

        val shiftMap = hashMapOf(
            "BID" to GlobalUserData.bid,
            "day" to shift.day,
            "employees" to shift.employees,
            "startTime" to shift.startTime,
            "endTime" to shift.endTime
        )

        db.collection("schedule")
            .add(shiftMap)
            .addOnSuccessListener { documentReference ->
                Log.d("AddShiftActivity", "Shift added with ID: ${documentReference.id}")
                val intent = Intent(this, SchedulerActivity::class.java)
                intent.putExtra("SELECTED_DATE", shift.day)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("AddShiftActivity", "Error adding shift", e)
            }
    }

    private fun getEmployeesFromDB() {
        db.collection("users")
            .whereEqualTo("BID", GlobalUserData.bid)
            .whereIn("account_status.status", listOf("Created", "Active"))
            .get()
            .addOnSuccessListener { documents ->
                val employeeNamesList = mutableListOf<String>()

                for (document in documents) {
                    val name = document.getString("name") ?: "N/A"
                    employeeNamesList.add(name)
                }
                employeeNames.clear()
                employeeNames.addAll(employeeNamesList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("ScheduleMakerActivity", "Error getting employee data", exception)
                Toast.makeText(this, "Failed to retrieve employees", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDatePicker() {
        // Create a MaterialDatePicker instance for date picking
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        // Set positive button click listener to handle date selection
        datePicker.addOnPositiveButtonClickListener { selectedDate ->
            // Convert the selected date to a formatted string
            val formattedDate = getDateFormattedFromMillis(selectedDate)

            // Set the selected date to the date TextView
            dateTextView.text = formattedDate
        }

        // Show the date picker
        datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
    }

    private fun showTimePicker(editText: EditText) {
        val isStartTime = editText.id == R.id.startTime

        // Create a MaterialTimePicker instance for time picking with 12-hour format
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setTitleText("Select ${if (isStartTime) "Start" else "End"} Time")
            .build()

        // Set positive button click listener to handle time selection
        timePicker.addOnPositiveButtonClickListener {
            val amPm = if (timePicker.hour < 12) "AM" else "PM"
            val hour12 = if (timePicker.hour % 12 == 0) 12 else timePicker.hour % 12
            val hour = if (hour12 < 10) "0$hour12" else hour12
            val minute = if (timePicker.minute < 10) "0${timePicker.minute}" else timePicker.minute
            val timeString = "$hour:$minute $amPm"

            // Set the selected time to the corresponding EditText
            editText.setText(timeString)
        }

        // Show the time picker
        timePicker.show(supportFragmentManager, "TIME_PICKER_TAG")
    }

    private fun getDateFormattedFromMillis(dateInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
