package com.example.optimate.employeeFlow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.optimate.businessOwner.XmlTopBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ViewHistoryScreen(showDateRangePicker: (updateDateRange: (String, String) -> Unit) -> Unit, workLogs: List<Map<String, Any>>)   {
    var dateRangeText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { XmlTopBar(titleText = "View Clock In / Out History") },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = dateRangeText,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    SelectDatesButton {

                        showDateRangePicker { startDate, endDate ->
                            dateRangeText = "$startDate - $endDate"
                        }
                    }

                }
                Spacer(modifier = Modifier.height(16.dp))

                if (workLogs.isEmpty()) {
                    Text(
                        text = "No work logs found",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(text = "Work Logs", style = MaterialTheme.typography.h5)
                    workLogs.forEach { workLog ->

                        Text(text = "Work Log: $workLog", modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun SelectDatesButton(onClick: () -> Unit) {
    val buttonColor = MaterialTheme.colors.run { Color(0xFF75f8e2) }
    Button(onClick = onClick,
        colors = ButtonDefaults.run { buttonColors(buttonColor) },
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 16.dp))
         {
        Text("Select Dates",fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)

    }
}

fun parseDate(date: String): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    return LocalDate.parse(date, formatter)
}



@Preview
@Composable
fun ViewHistoryScreenPreview() {
    ViewHistoryScreen(showDateRangePicker = {}, workLogs = listOf())
}
