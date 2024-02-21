package com.example.optimate.businessOwner

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

class AddShiftActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Update the UI with the latest values
            var employees = listOf("Bob", "Sue", "Jim")

            AddShiftPage(
                employees = employees,
                onShiftAdded = {}
            )
        }
    }
}