package com.example.optimate.businessOwner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme

class TitlesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val sampleRoles = listOf(
                    Role("Manager", "Managers:"),
                    Role("Head Chef", "Managers:"),
                    Role("Server", "Employees:"),
                    Role("Line Cook", "Employees:"),
                    Role("Dishwasher", "Employees:"),
                    Role("Manager", "Managers:"),
                    Role("Head Chef", "Managers:"),
                    Role("Server", "Employees:"),
                    Role("Line Cook", "Employees:"),
                    Role("Dishwasher", "Employees:"),
                    Role("Manager", "Managers:"),
                    Role("Head Chef", "Managers:"),
                    Role("Server", "Employees:"),
                    Role("Line Cook", "Employees:"),
                    Role("Dishwasher", "Employees:"))
                TitlesScreen(sampleRoles) // Pass your roles list here
            }
        }
    }
}
