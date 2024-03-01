package com.example.optimate.businessOwner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleMakerActivity : AppCompatActivity() {
    private var db = Firebase.firestore
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

    private lateinit var dynamicContentContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_maker)

        val topBar: XmlTopBar = findViewById(R.id.topBar)
        topBar.setTitle("Daily Schedule")
        val selectedDate = intent.getStringExtra("SELECTED_DATE")

        // Find the dynamic content container

        dynamicContentContainer = findViewById(R.id.dynamicContentContainer)
        // Add multiple instances of content_schedule_maker dynamically
        if (selectedDate != null) {
            fetchShiftData(selectedDate)

        }

        val editTextDate: TextView = findViewById(R.id.editTextDate)

        // Retrieve the selected date from the Intent extras


        // Set the text of editTextDate to the selected date
        editTextDate.setText(selectedDate)

        // Find the FAB button
        val addShiftBtn: FloatingActionButton = findViewById(R.id.addShiftBtn)

        // Set click listener for the FAB button to navigate to the Add Shift page
        addShiftBtn.setOnClickListener {
            // Create an Intent to start ScheduleMakerActivity
            val intent = Intent(this, AddShiftActivity::class.java)

            // Pass the selected date to ScheduleMakerActivity using Intent extras
            intent.putExtra("SELECTED_DATE", selectedDate)

            // Start ScheduleMakerActivity
            startActivity(intent)
            finish()
        }
    }

    private fun fetchShiftData(selectedDate: String) {
        // Fetch shift data using the selected date
        getShiftData(selectedDate) { shifts ->
            populateUI(shifts)
        }
    }

    private fun populateUI(shifts: List<Shift>) {

        if (!::dynamicContentContainer.isInitialized) {
            // Initialize dynamic content container if not initialized
            dynamicContentContainer = findViewById(R.id.dynamicContentContainer)
        }

        // Sort the list of shifts by start time
        val sortedShifts = shifts.sortedBy {  convertStringToTime(it.startTime)  }


        for (shift in sortedShifts) {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val contentView = inflater.inflate(R.layout.content_schedule_maker, null)
            dynamicContentContainer.addView(contentView)

            contentView.findViewById<TextView>(R.id.shiftHours).text = "${shift.startTime} - ${shift.endTime}"
            val names = StringBuilder()
            for (name in shift.employees!!) { // Assuming names is a list of names in Shift class
                names.append(name).append(" \n") // You can adjust the separator as needed
            }

            // Remove the trailing comma and space if there are names
            if (names.isNotEmpty()) {
                names.setLength(names.length - 2)
            }

            contentView.findViewById<TextView>(R.id.NameFromDb).text = names.toString()
        }
    }

    private fun getShiftData(selectedDate: String, callback: (List<Shift>) -> Unit) {
        db.collection("schedule")
            .whereEqualTo("BID", GlobalUserData.bid)
            .whereEqualTo("day", selectedDate)
            .get()
            .addOnSuccessListener { documents ->
                val shiftsList = mutableListOf<Shift>()
                for (document in documents) {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    // Assuming Shift is a model class representing your shift data
                    val shift = document.toObject(Shift::class.java)
                    shiftsList.add(shift)
                }

                // Pass the list of Shift objects to the callback function
                callback(shiftsList)
            }
            .addOnFailureListener { exception ->
                Log.e("ScheduleMakerActivity", "Error getting shift data", exception)
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()

            }
    }

    private fun convertStringToTime(timeStr: String): Date {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.parse(timeStr) ?: Date()
    }
}
