package com.example.optimate.businessOwner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddShiftPage(
    onShiftAdded: (Shift) -> Unit
) {
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedEmployee by remember { mutableStateOf<String?>(null) }
    var startTime by remember { mutableStateOf("09:00 AM") }
    var endTime by remember { mutableStateOf("05:00 PM") }

    Scaffold(
        topBar = { XmlTopBar(titleText = "Scheduler") },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                TextField(
                    value = selectedEmployee ?: "",
                    onValueChange = { selectedEmployee = it },
                    label = { Text("Select Employee") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                TextField(
                    value = selectedDate ?: "",
                    onValueChange = { selectedDate = it },
                    label = { Text("Select Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                TextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Start Time") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                TextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("End Time") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val newShift = Shift(
                            day = selectedDate ?: "Unknown",
                            employees = listOf(selectedEmployee ?: "Unknown"),
                            startTime = startTime,
                            endTime = endTime
                        )
                        onShiftAdded(newShift)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Save Shift")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddShiftPagePreview() {
    AddShiftPage(
        onShiftAdded = {}
    )
}
