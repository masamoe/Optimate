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
    val shiftsList = mutableListOf<Shift>()

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_maker)

        val topBar: XmlTopBar = findViewById(R.id.topBar)
        topBar.setTitle("Daily Schedule")
        val selectedDate = intent.getStringExtra("SELECTED_DATE")

        // Find the dynamic content container
        val dynamicContentContainer: LinearLayout = findViewById(R.id.dynamicContentContainer)

        // Add multiple instances of content_schedule_maker dynamically
        if (selectedDate != null) {
            getShiftData(selectedDate) { shifts ->
                for (shift in shiftsList) {
                    val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val contentView = inflater.inflate(R.layout.content_schedule_maker, null)
                    dynamicContentContainer.addView(contentView)

                    contentView.findViewById<TextView>(R.id.shiftHours).text = "${shift.startTime} - ${shift.endTime}"
                }
             }

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
        }
    }

    private fun getShiftData(selectedDate: String, callback: (Shift) -> Unit) {
        db.collection("schedule")
            .whereEqualTo("BID", GlobalUserData.bid)
            .whereEqualTo("day", selectedDate)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    // Assuming Shift is a model class representing your shift data
                    val shift = document.toObject(Shift::class.java)
                    shiftsList.add(shift)
                    callback(shift)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ScheduleMakerActivity", "Error getting shift data", exception)
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()

            }
    }
}
