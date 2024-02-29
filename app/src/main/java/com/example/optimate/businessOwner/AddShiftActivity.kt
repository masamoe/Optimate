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
import com.example.optimate.R
import com.example.optimate.loginAndRegister.GlobalUserData
import com.example.optimate.loginAndRegister.NewUserPasswordChange
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class AddShiftActivity : AppCompatActivity() {

    private val employeeNames = arrayOf("Bob", "Sue", "Jim", "Alice", "John")
    private var db = Firebase.firestore

    data class Shift(
        val day: CharSequence,
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

        // Retrieve the selected date from the Intent extras
        val selectedDate = intent.getStringExtra("SELECTED_DATE")

        // Set the text of editTextDate to the selected date
        date.text = selectedDate

        val startTime = findViewById<EditText>(R.id.startTime)

        val endTime = findViewById<EditText>(R.id.endTime)

        val employeeListView: ListView = findViewById(R.id.employeeListView)

        val adapter = ArrayAdapter<String>(
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
            saveShiftToFirebase(date.text, startTime, endTime)

        }
    }


    private fun saveShiftToFirebase(
        date: CharSequence,
        startTimeEditText: EditText,
        endTimeEditText: EditText
    ) {
        val startTime = startTimeEditText.text.toString()
        val endTime = endTimeEditText.text.toString()

        val shift = Shift(
            day = date.toString(),
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
                Toast.makeText(this, "Shift added successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ScheduleMakerActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("AddShiftActivity", "Error adding shift", e)
                Toast.makeText(this, "Failed to add shift", Toast.LENGTH_SHORT).show()
            }
    }
}
