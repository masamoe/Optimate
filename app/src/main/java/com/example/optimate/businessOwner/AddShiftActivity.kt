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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddShiftActivity : AppCompatActivity() {

    private var employeeNames = mutableStateListOf<String>()

    private var db = Firebase.firestore
    private lateinit var adapter: ArrayAdapter<String>

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

        val date: TextView = findViewById(R.id.date)
        val selectedDate = intent.getLongExtra("SELECTED_DATE", 0)

        // Convert the selected date to a formatted string
        val formattedDate = getDateFormattedFromMillis(selectedDate)
        // Set the text of editTextDate to the selected date
        date.text = formattedDate

        val startTime = findViewById<EditText>(R.id.startTime)
        val endTime = findViewById<EditText>(R.id.endTime)
        val employeeListView: ListView = findViewById(R.id.employeeListView)

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
            saveShiftToFirebase(formattedDate, startTime, endTime)
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
        val timeRegex = Regex("""^(?:[01]\d|2[0-3]):(?:[0-5]\d)$""")
        if (!startTime.matches(timeRegex) || !endTime.matches(timeRegex)) {
            // If either startTime or endTime doesn't match the hh:mm format, show an error
            Toast.makeText(
                this,
                "Invalid time format. Please use HH:MM format.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

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

    private fun getDateFormattedFromMillis(dateInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
