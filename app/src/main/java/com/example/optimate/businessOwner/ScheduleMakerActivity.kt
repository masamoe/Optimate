package com.example.optimate.businessOwner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ScheduleMakerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_maker)

        val topBar: XmlTopBar = findViewById(R.id.topBar)
        topBar.setTitle("Daily Schedule")

        // Find the dynamic content container
        val dynamicContentContainer: LinearLayout = findViewById(R.id.dynamicContentContainer)

        // Add multiple instances of content_schedule_maker dynamically
        for (i in 1..4) {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val contentView = inflater.inflate(R.layout.content_schedule_maker, null)
            dynamicContentContainer.addView(contentView)
        }

        val editTextDate: TextView = findViewById(R.id.editTextDate)

        // Retrieve the selected date from the Intent extras
        val selectedDate = intent.getStringExtra("SELECTED_DATE")

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
}
