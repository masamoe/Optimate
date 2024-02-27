package com.example.optimate.businessOwner

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.optimate.R

class AddShiftActivity : AppCompatActivity() {

    private val employeeNames = arrayOf("Bob", "Sue", "Jim", "Alice", "John")
    data class Shift(
        val day: String,
        val employees: List<String>,
        val startTime: String,
        val endTime: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_shift)

        val topBar: XmlTopBar = findViewById(R.id.topBar)
        topBar.setTitle("Add Shift")

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
            val isChecked = employeeListView.isItemChecked(position)
            // Perform any additional actions based on selection/deselection
        }

    }

    private fun saveShiftToFirebase(shift: Shift) {
        // Implement the logic to save the shift information to Firebase
        // For example, use Firebase Realtime Database or Firestore here
        // You may need to initialize Firebase and set up your database before this point
        // ...
    }
}
