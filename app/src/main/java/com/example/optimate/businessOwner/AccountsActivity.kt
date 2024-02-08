package com.example.optimate.businessOwner

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

class AccountsActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountsScreen(
                accounts = listOf(
                    Account("John Doe", "Accountant"),
                    Account("Jane Doe", "Accountant"),
                    Account("John Doe", "Accountant"),
                    Account("Jane Doe", "Accountant"),
                    Account("John Doe", "Accountant"),
                    Account("Jane Doe", "Accountant"),
                    Account("John Doe", "Accountant"),
                    Account("Jane Doe", "Accountant"),
                    Account("John Doe", "Accountant"),
                    Account("Jane Doe", "Accountant"),
                    Account("John Doe", "Accountant"),
                    Account("Jane Doe", "Accountant"),
                    Account("John Doe", "Accountant"),
                    Account("Jane Doe", "Accountant"),
                    Account("John Doe", "Accountant"),
                    Account("Jane Doe", "Accountant"),
                    Account("John Doe", "Accountant"),
                    Account("Jane Doe", "Accountant"),
                    Account("John Doe", "Accountant"),
                    Account("Jane Doe", "Accountant"),
                )
            )
        }
    }
}