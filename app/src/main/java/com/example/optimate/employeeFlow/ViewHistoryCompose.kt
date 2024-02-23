package com.example.optimate.employeeFlow

import android.os.Build
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.optimate.businessOwner.AccountRow
import com.example.optimate.businessOwner.FinancesRow
import com.example.optimate.businessOwner.XmlTopBar
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

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

                TotalHours(workLogs)

                Spacer(modifier = Modifier.height(16.dp))

                if (workLogs.isEmpty()) {
                    NoDataFound("No work logs found.")
                } else {
                    WorkLogList(workLogs)

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

@Composable
fun WorkLogList(workLogs: List<Map<String, Any>>) {
    LazyColumn {
        itemsIndexed(workLogs) { index, workLog ->
            // Cast the workLog item to the correct type
            val typedWorkLog = workLog.mapValues { entry ->
                // Assuming the value is a List<*>, cast each item in the list to Map<String, String>
                @Suppress("UNCHECKED_CAST")
                (entry.value as List<Map<String, String>>)
            }

            WorkLogsRow(typedWorkLog)
        }
    }
}

@Composable
fun WorkLogsRow(workLog: Map<String, List<Map<String, String>>>) {
    val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())

    fun formatDate(dateString: String): String {
        return if (dateString.length == 8) {
            "${dateString.substring(0, 4)}/${dateString.substring(4, 6)}/${dateString.substring(6, 8)}"
        } else {
            dateString // Return the original string if it's not in the expected format
        }
    }

    // Convert map entries to a list and iterate with index
    workLog.entries.toList().forEachIndexed { index, (date, logs) ->
        var isExpanded by remember { mutableStateOf(false) }
        var totalDurationInMillis = 0L

        logs.forEach { log ->
            var clockInTime: Long? = null
            var clockOutTime: Long? = null
            var breakStartTime: Long? = null
            var breakEndTime: Long? = null

            log.forEach { (key, value) ->
                when (key) {
                    "clockIn" -> clockInTime = dateFormat.parse(value)?.time
                    "clockOut" -> clockOutTime = dateFormat.parse(value)?.time
                    "breakStart" -> breakStartTime = dateFormat.parse(value)?.time
                    "breakEnd" -> breakEndTime = dateFormat.parse(value)?.time
                }
            }

            val workDuration = (clockOutTime ?: 0L) - (clockInTime ?: 0L)
            val breakDuration = (breakEndTime ?: 0L) - (breakStartTime ?: 0L)

            totalDurationInMillis += (workDuration - breakDuration)
        }

        val totalHours = TimeUnit.MILLISECONDS.toHours(totalDurationInMillis)
        val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(totalDurationInMillis) % 60
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(totalDurationInMillis) % 60

        // Determine the background color based on the index
        val backgroundColor = if (index % 2 == 0) Color(0xFFC0C2EC) else Color(0xFFF2EBF3)
        Card(
            backgroundColor = backgroundColor,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded } // Toggle isExpanded on click
        ) {
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    val styledText = buildAnnotatedString {
                        append(formatDate(date) + " ")
                        // Apply a SpanStyle to "Total Hours" part
                        withStyle(style = SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Light, color = Color.Blue)) {
                            append("(Total Hours: %02d:%02d:%02d)".format(totalHours, totalMinutes, totalSeconds))
                        }
                    }
                    Text(text = styledText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.clickable { isExpanded = !isExpanded }
                    )
                }

                // Expandable content for each card
                if (isExpanded) {
                    logs.forEach { log ->
                        log.forEach { (key, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                if (key =="clockIn")
                                    Text(text = "Clock In:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                if (key =="clockOut")
                                    Text(text = "Clock Out:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                if (key =="breakStart")
                                    Text(text = "Break Start:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                if (key =="breakEnd")
                                    Text(text = "Break End:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color.Blue)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TotalHours(workLogs: List<Map<String, Any>>) {
    val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())
    var totalDurationInMillis = 0L

    // Assuming all logs are within the first element of the list
    if (workLogs.isNotEmpty()) {
        val allLogs = workLogs.first() // Get the first (and presumably only) map

        // Iterate through each date's logs in the map
        allLogs.forEach { (_, logs) ->
            @Suppress("UNCHECKED_CAST")
            val logList = logs as List<Map<String, String>>

            var clockInTime: Long? = null
            var clockOutTime: Long? = null
            var breakStartTime: Long? = null
            var breakEndTime: Long? = null

            logList.forEach { log ->
                log.forEach { (key, value) ->
                    when (key) {
                        "clockIn" -> clockInTime = dateFormat.parse(value)?.time
                        "clockOut" -> clockOutTime = dateFormat.parse(value)?.time
                        "breakStart" -> breakStartTime = dateFormat.parse(value)?.time
                        "breakEnd" -> breakEndTime = dateFormat.parse(value)?.time
                    }
                }
            }

            val workDuration = (clockOutTime ?: 0L) - (clockInTime ?: 0L)
            val breakDuration = (breakEndTime ?: 0L) - (breakStartTime ?: 0L)

            totalDurationInMillis += (workDuration - breakDuration)
        }
    }

    val totalHours = TimeUnit.MILLISECONDS.toHours(totalDurationInMillis)
    val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(totalDurationInMillis) % 60
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(totalDurationInMillis) % 60

    Text(
        text = "Total Hours: %02d:%02d:%02d".format(totalHours, totalMinutes, totalSeconds),
        modifier = Modifier.padding(start = 16.dp),
        color = Color.Black,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun NoDataFound(text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
            .padding(top = 100.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Help,
            contentDescription = "No Data",
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    }
}


//sample data
val theList = listOf(
    mapOf(
        "20240223" to listOf(
            mapOf("clockIn" to "02/23/2024 12:33:21"),
            mapOf("breakStart" to "02/23/2024 12:43:21"),
            mapOf("breakEnd" to "02/23/2024 12:53:21"),
            mapOf("clockOut" to "02/23/2024 22:33:21")
        ),
        "20240224" to listOf(
            mapOf("clockIn" to "02/24/2024 12:33:21"),
            mapOf("breakStart" to "02/24/2024 12:43:21"),
            mapOf("breakEnd" to "02/24/2024 12:53:21"),
            mapOf("clockOut" to "02/24/2024 22:33:21")
        )
    )
)

@Preview
@Composable
fun ViewHistoryScreenPreview() {
    ViewHistoryScreen(
        showDateRangePicker = { }, workLogs = theList
    )
}
